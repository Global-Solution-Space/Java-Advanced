# AGENT.md — Terranova API

> Guia de arquitetura e convenções do projeto para agentes de IA e desenvolvedores.

---

## 📋 Visão Geral

**Terranova** é uma API REST de monitoramento agrícola inteligente, construída com Spring Boot 4.x + Java 17. Ela integra dados de satélite (SatVeg da Embrapa), dados climáticos (NASA POWER) e informações de propriedades rurais para gerar alertas agrícolas.

- **Group ID:** `fiap.com.br`
- **Artifact:** `terranova`
- **Base package:** `fiap.com.br.terranova`
- **Banco:** H2 (dev) — estrutura modelada para Oracle (DDL de referência)

---

## 🏗️ Arquitetura

```
fiap.com.br.terranova
├── exception/                  # Exceções globais (GlobalExceptionHandler)
├── integration/                # Clients OpenFeign (APIs Externas: NASA, SatVeg)
│   ├── nasa/                   # NasaPowerClient + NasaPowerDataResponse
│   └── satveg/                 # SatVegClient + SatVegDataRequest/Response
├── validation/                 # Validações customizadas (UniqueEmail, ValidTalhaoArea, EnumValidation)
├── alerta/                     # Alertas agrícolas
├── dadotemporal/               # Tabela Fato — séries temporais normalizadas
├── localizacao/                # Coordenadas geográficas
├── produtor/                   # Produtores rurais
├── propriedade/                # Propriedades rurais
├── reqapi/                     # Requisições de API (substitui nasapower/ e satveg/)
├── talhao/                     # Talhões (parcelas de terra)
├── telefone/                   # Telefones dos produtores
├── tipoapi/                    # Tipos de API (SATVEG, NASAPOWER) — seed automático
└── tipoplantacao/              # Tipos de plantação
```

Cada pacote de domínio segue a mesma estrutura interna:

```
<dominio>/
├── <Entidade>.java             # Entity JPA
├── <Entidade>Repository.java   # JpaRepository
├── <Entidade>Service.java      # Lógica de negócio (@Transactional)
├── <Entidade>Controller.java   # REST Controller
└── dto/
    ├── <Entidade>Request.java  # DTO de entrada (record)
    └── <Entidade>Response.java # DTO de saída (record)
```

---

## 📐 Padrões e Convenções

### Entities

- **Anotações obrigatórias:** `@Entity`, `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Table`
- **Campos em camelCase** — mapeados para snake_case via `@Column(name = "...")`
- **IDs:** `Long` com `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- **Relacionamentos:** `@ManyToOne` / `@OneToOne` com `@JoinColumn` usando o nome exato da FK do DDL
- **Não usar `@Data` com `@Builder` é ERRADO** — ambos devem estar presentes

### DTOs — Java Records

**NUNCA usar classes com Lombok (`@Data`) para DTOs.** Todos os DTOs são Java `record`.

#### Request (entrada)

- Usa **Bean Validation** (`@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Pattern`, `@Positive`, `@DecimalMax`, etc.)
- **Validações Customizadas:** `@UniqueEmail` (email único), `@ValidTalhaoArea` (soma de áreas), `@EnumValidation` (enum check)
- Contém método **`toEntity(...)`** que recebe as entidades de FK já resolvidas.
- FKs são representadas como `Long idRelacao` (ID puro, não a entidade).

#### Response (saída)

- **Imutável** — apenas dados serializáveis
- Contém **`static fromEntity(Entidade)`** que extrai IDs das relações
- **Implementa HATEOAS:** Possui o método **`toEntityModel()`** que retorna um `EntityModel<T>` preenchido com links estáticos.

### Services

- **`@Service` + `@RequiredArgsConstructor`** — injeção via construtor final (Lombok)
- Transações gerenciadas por **`@Transactional`** nos métodos `create`, `update` e `delete`.
- **Paginação:** métodos `findAll` retornam `Page<Response>` com `Pageable`
- **Delete otimizado:** `repository.delete(entity)` (após validar se a entidade existe).
- **APIs Externas:** O `ReqApiService` centraliza TODA a lógica de integração com NASA POWER e SatVeg. A decisão de chamada é feita via `switch` a partir do campo `tipoApiNome` enviado pelo mobile (validado via `@EnumValidation`).

### Validações Customizadas (pacote `validation/`)

| Anotação | Target | Lógica |
|----------|--------|--------|
| `@UniqueEmail` | Campo `email` | Verifica `existsByEmail()`. Tolera PUT do próprio ID. |
| `@ValidTalhaoArea` | Classe `TalhaoRequest` | Soma áreas dos talhões da propriedade e valida contra `tamanhoTotal`. |
| `@EnumValidation` | Campos String | Verifica se valor está contido nos nomes do enum informado. |

### Integrações (OpenFeign)

- Clientes criados como `interface` com `@FeignClient`.
- Para métodos GET complexos, utiliza-se `@SpringQueryMap Map<String, Object>`.
- Respostas mapeadas em Records exclusivos da API.

### Controllers

- **`@RestController` + `@RequestMapping("/api/<recurso>")` + `@RequiredArgsConstructor`**
- **`@Valid`** em todos os `@RequestBody`
- **`Pageable`** no endpoint de listagem (GET sem ID)
- **HTTP Status:** `201 CREATED` no POST, `204 NO_CONTENT` no DELETE, `200 OK` nos demais
- **Retornos HATEOAS:** Envelopados em `EntityModel<T>` ou `Page<EntityModel<T>>`.
- **CORS:** Liberado globalmente via `CorsConfig`.
- **Otimização de Overfetching:** Para evitar que o Mobile baixe toda a base do sistema, existem endpoints específicos de filtragem por `idProdutor` que retornam coleções HATEOAS (`CollectionModel<EntityModel<T>>`):
  - `GET /api/propriedades/produtor/{idProdutor}`
  - `GET /api/talhoes/produtor/{idProdutor}`
  - `GET /api/alertas/produtor/{idProdutor}`

---

## 🗄️ Modelo de Dados (Relações)

```
Produtor (1) ←──── (1) Telefone
    │
    ▼ (1:N)
Propriedade ────── (1:1) Localizacao
    │
    ▼ (1:N)
Talhao ────── (1:1) Localizacao
  │  │
  │  └──── (N:1) TipoPlantacao
  │
  ├──── (1:N) AlertaAgricola
  │
  └──── (1:N) DadoTemporal ────── (N:1) ReqApi ────── (N:1) TipoApi
```

### Tabela de FKs (nomes DDL)

| Tabela | FK Column (DDL) | Referencia |
|--------|----------------|------------|
| `telefone` | `produtor_id_produtor` | `produtor.id_produtor` |
| `propriedade` | `produtor_id_produtor` | `produtor.id_produtor` |
| `propriedade` | `localizacao_id_localizacao` | `localizacao.id_localizacao` |
| `talhao` | `tipo_plantacao_id_tipo_plant` | `tipo_plantacao.id_tipo_plant` |
| `talhao` | `propriedade_id_propriedade` | `propriedade.id_propriedade` |
| `talhao` | `localizacao_id_localizacao` | `localizacao.id_localizacao` |
| `alerta_agricola` | `talhao_id_talhao` | `talhao.id_talhao` |
| `dado_temporal` | `talhao_id_talhao` | `talhao.id_talhao` |
| `dado_temporal` | `req_api_id_api` | `req_api.id_api` |
| `req_api` | `tipo_api_id_tipo` | `tipo_api.id_tipo` |

### Enums

| Enum | Pacote | Valores |
|------|--------|---------|
| `NivelAlerta` | `alerta` | BAIXO, MEDIO, ALTO, CRITICO |
| `TipoParam` | `reqapi` | NVDI, PRECTOTCORR |
| `TipoApiEnum` | `tipoapi` | SATVEG, NASAPOWER |

---

## 📦 Dependências Principais

| Dependência | Uso |
|-------------|-----|
| `spring-boot-starter-data-jpa` | JPA + Hibernate |
| `spring-boot-starter-webmvc` | REST Controllers |
| `spring-boot-starter-validation` | Bean Validation (`@Valid`) |
| `spring-cloud-starter-openfeign`| Clients HTTP Declarativos |
| `spring-boot-starter-hateoas` | HATEOAS (disponível) |
| `springdoc-openapi-starter-webmvc-ui` | Swagger UI (`/swagger-ui.html`) |
| `lombok` | `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j` |
| `h2` | Banco em memória para dev |

---

## ⚠️ Regras Importantes

1. **DTOs são SEMPRE `record`** — nunca `class` com `@Data`
2. **Entities são SEMPRE `class`** com `@Data` + `@Builder`
3. **Nunca expor entidades JPA diretamente** nos endpoints — sempre usar Request/Response
4. **Tratamento de Exceções:** Centralizado em `GlobalExceptionHandler`.
5. **Transações:** `@Transactional` é vital em operações de escrita nos Services.
6. **Tipos numéricos de área:** `Double` (não `BigDecimal`).
7. **Campo `resolvido` do Alerta:** `Integer` (0 ou 1), mapeado como `NUMBER` no Oracle.
8. **TipoApi:** Seed automático via `DatabaseInitializer` (`CommandLineRunner`) — NUNCA expor endpoint de escrita.

---

## 🧪 Para rodar

```bash
./mvnw spring-boot:run
```

- **API:** `http://localhost:8080`
- **Swagger:** `http://localhost:8080/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/h2-console`
