# AGENT.md - Terranova API

Guia de arquitetura, convencoes e estado atual do projeto para agentes de IA e desenvolvedores.

---

## Visao Geral

**Terranova** e uma API REST de monitoramento agricola inteligente, construida com Spring Boot 4.x e Java 17. O sistema organiza produtores, telefones, localizacoes, propriedades, talhoes, tipos de plantacao, requisicoes de APIs externas, dados temporais e alertas agricolas.

A integracao externa acontece por meio do `ReqApiService`, que consulta NASA POWER ou Embrapa SATVeg, persiste os pontos como `DadoTemporal` e aciona a analise automatica de alertas.

- **Group ID:** `fiap.com.br`
- **Artifact:** `terranova`
- **Base package:** `fiap.com.br.terranova`
- **Banco local:** H2 em memoria
- **Perfil de entrega:** API Spring Boot com JPA, Bean Validation, HATEOAS, OpenFeign e Swagger

---

## Arquitetura

```text
fiap.com.br.terranova
|-- config/                     # CORS, OpenAPI e seed inicial
|-- exception/                  # GlobalExceptionHandler e ResourceNotFoundException
|-- integration/                # Clients OpenFeign
|   |-- nasa/                   # NasaPowerClient + NasaPowerDataResponse
|   `-- satveg/                 # SatVegClient + SatVegDataRequest/Response
|-- validation/                 # Validadores customizados
|-- alerta/                     # Alertas agricolas
|-- dadotemporal/               # Series temporais persistidas
|-- localizacao/                # Coordenadas geograficas
|-- produtor/                   # Produtores rurais
|-- propriedade/                # Propriedades rurais
|-- reqapi/                     # Requisicoes de API e TipoParam
|   `-- tipoapi/                # TipoApi, TipoApiEnum e TipoApiRepository
|-- talhao/                     # Talhoes
|-- telefone/                   # Telefones dos produtores
`-- tipoplantacao/              # Tipos de plantacao
```

Pacotes de dominio seguem, em geral, esta estrutura:

```text
<dominio>/
|-- <Entidade>.java
|-- <Entidade>Repository.java
|-- <Entidade>Service.java
|-- <Entidade>Controller.java
`-- dto/
    |-- <Entidade>Request.java
    `-- <Entidade>Response.java
```

Excecoes relevantes:

- `dadotemporal/` nao tem DTO de request nem endpoints de escrita. Os dados temporais sao criados pela integracao externa via `ReqApiService`.
- `reqapi/tipoapi/` fica dentro do pacote `reqapi`, nao em um pacote top-level separado.

---

## Modelo de Dominio

```text
Produtor (1) ---- (N) Telefone
    |
    `---- (N) Propriedade ---- (1) Localizacao
               |
               `---- (N) Talhao ---- (1) Localizacao
                         |
                         |---- (N:1) TipoPlantacao
                         |---- (1:N) Alerta
                         `---- (1:N) DadoTemporal ---- (N:1) ReqApi ---- (N:1) TipoApi
```

Principais regras:

- Um produtor pode ter varias propriedades e telefones.
- Uma propriedade e um talhao referenciam uma localizacao.
- Um talhao pertence a uma propriedade e a um tipo de plantacao.
- Alertas pertencem a talhoes.
- Dados temporais pertencem a talhoes e a uma requisicao de API.
- `ReqApi` registra a chamada externa e referencia `TipoApi`.

---

## Endpoints Atuais

Base URL local: `http://localhost:8080`

### CRUDs com paginacao

Estes recursos possuem `GET`, `GET /{id}`, `POST`, `PUT /{id}` e `DELETE /{id}`:

- `/api/localizacoes`
- `/api/produtores`
- `/api/telefones`
- `/api/propriedades`
- `/api/tipos-plantacao`
- `/api/talhoes`
- `/api/alertas`

### Filtros especificos

- `GET /api/propriedades/produtor/{idProdutor}`
- `GET /api/talhoes/produtor/{idProdutor}`
- `GET /api/alertas/produtor/{idProdutor}`
- `GET /api/req-api/talhao/{idTalhao}`
- `GET /api/dados-temporais/talhao/{idTalhao}`
- `GET /api/dados-temporais/req-api/{idReqApi}`

### Recursos especiais

- `POST /api/req-api`: dispara integracao externa, persiste dados temporais e analisa alertas.
- `DELETE /api/req-api/{id}`: remove uma requisicao de API.
- `GET /api/dados-temporais`: lista todos os dados temporais.
- `GET /api/dados-temporais/{id}`: busca um dado temporal por ID.
- `PATCH /api/alertas/{id}/resolver`: marca alerta como resolvido (`S`).
- `PATCH /api/alertas/{id}/reabrir`: reabre alerta (`N`).

Nao existe `PUT /api/req-api/{id}` no controller atual.

---

## DTOs e Validacao

### DTOs

- DTOs de entrada e saida usam Java `record`.
- Requests usam Bean Validation (`@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Pattern`, `@Positive`, `@DecimalMax`, `@DecimalMin`).
- Requests convertem para entidade com `toEntity(...)`.
- Responses convertem entidade para DTO com `fromEntity(...)`.
- Responses expostos por controllers sao empacotados com HATEOAS via `toEntityModel()`.

### Validadores customizados

| Validador | Aplicacao | Comportamento |
| --- | --- | --- |
| `@UniqueEmail` | `ProdutorRequest.email` | Verifica unicidade do e-mail e tolera update do proprio produtor. |
| `@ValidPropriedadeArea` | `PropriedadeRequest` | Garante que o tamanho total nao fique menor que a soma dos talhoes existentes. |
| `@ValidTalhaoArea` | `TalhaoRequest` | Garante que a area do talhao caiba na propriedade. |
| `@EnumValidation` | Campos `String` | Valida o valor contra o enum informado. |
| `@BrasilCoordenadas` | `LocalizacaoRequest` | Valida coordenadas por range e consulta externa de geocoding. Em falha da API externa, nao bloqueia o usuario. |

---

## Services

- Services usam `@Service` e `@RequiredArgsConstructor`.
- Operacoes de escrita usam `@Transactional`.
- Deletes buscam a entidade antes e chamam `repository.delete(entity)`.
- `findAll(Pageable)` retorna `Page<Response>` na maioria dos dominios.
- `DadoTemporalService.findAll()` retorna `List<DadoTemporalResponse>`, pois o controller usa `CollectionModel`.

### ReqApiService

`ReqApiService` centraliza a integracao externa:

- Resolve `TipoApi` por nome (`SATVEG` ou `NASAPOWER`).
- Resolve o `Talhao` pelo ID recebido.
- Salva a `ReqApi`.
- Chama NASA POWER ou SATVeg.
- Converte os pontos retornados para `DadoTemporal`.
- Persiste os dados temporais.
- Chama `AlertaService.analisarEGerarAlertas(...)`.

---

## Alertas Automaticos

`AlertaService.analisarEGerarAlertas(Talhao talhao, String tipoApiNome)` aplica regras historicas:

- `NASAPOWER`: janela de 15 dias.
  - Chuva dos ultimos 3 dias acima de 80 mm gera **Risco de Alagamento (NASA)** com nivel `ALTO`.
  - Chuva acumulada em 15 dias abaixo de 10 mm gera **Seca Severa (NASA)** com nivel `CRITICO`.
  - Chuva acumulada em 15 dias abaixo de 25 mm gera **Estresse Hidrico (NASA)** com nivel `MEDIO`.
- `SATVEG`: janela de 90 dias.
  - NDVI mais recente abaixo de 0.2 gera **Anomalia Vegetativa Severa (SATVEG)** com nivel `CRITICO`.
  - NDVI mais recente abaixo de 0.4 gera **Baixo Vigor Vegetativo (SATVEG)** com nivel `MEDIO`.

Alertas automaticos nao sao duplicados quando ja existe um alerta ativo com o mesmo talhao, titulo e `resolvido = "N"`.

---

## Enums e Valores

| Enum | Pacote | Valores |
| --- | --- | --- |
| `NivelAlerta` | `alerta` | `BAIXO`, `MEDIO`, `ALTO`, `CRITICO` |
| `SimNao` | `alerta` | `S`, `N` |
| `TipoParam` | `reqapi` | `NDVI`, `PRECTOTCORR` |
| `TipoApiEnum` | `reqapi.tipoapi` | `SATVEG`, `NASAPOWER` |

Observacoes:

- `Alerta.resolvido` e `String` de tamanho 1, usando `S` ou `N`.
- `TipoParam` correto e `NDVI`, nao `NVDI`.

---

## Seed Inicial

`DatabaseInitializer` popula automaticamente:

- `TipoApi`: `SATVEG`, `NASAPOWER`.
- `TipoPlantacao`: `Soja`, `Milho`, `Algodao`, `Cafe`, `Cana-de-Acucar`, `Trigo`, `Arroz`, `Feijao`, `Laranja`, `Uva`.

Nao ha controller para escrita de `TipoApi`; ele e usado internamente por `ReqApiService`.

---

## Dependencias Principais

| Dependencia | Uso |
| --- | --- |
| `spring-boot-starter-data-jpa` | JPA e Hibernate |
| `spring-boot-starter-webmvc` | API REST |
| `spring-boot-starter-validation` | Bean Validation |
| `spring-cloud-starter-openfeign` | Clients HTTP declarativos |
| `spring-boot-starter-hateoas` | Links HATEOAS |
| `springdoc-openapi-starter-webmvc-ui` | Swagger UI |
| `lombok` | Builders, getters/setters e construtores |
| `h2` | Banco em memoria local |

---

## Testes

A suite cobre controllers, services, validadores, exceptions e DTOs importantes. A validacao completa ja foi feita com:

```bash
mvn clean test
```

Resultado verificado: `190` testes, `0` falhas, `0` erros.

Observacao: no ambiente atual, o `mvnw.cmd` falhou antes de iniciar Maven. Use `mvn` diretamente se o wrapper apresentar o mesmo problema.

---

## Para Rodar

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

---

## Regras Importantes

1. Nao expor entidades JPA diretamente nos endpoints.
2. Manter DTOs como `record`.
3. Manter entidades como classes JPA com Lombok.
4. Manter tratamento de erro centralizado em `GlobalExceptionHandler`.
5. Usar `@Transactional` em operacoes de escrita nos services.
6. Nao criar endpoint de escrita para `TipoApi` sem mudar explicitamente o desenho atual.
7. Nao adicionar update em `ReqApi` sem implementar controller, service e contrato de integracao.
8. Se alterar controllers, atualizar tambem `insomnia.yaml` e os testes correspondentes.
