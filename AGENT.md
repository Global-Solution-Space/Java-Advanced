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
├── alerta/                     # Alertas agrícolas
├── dadotemporal/               # Persistência relacional de séries temporais
├── localizacao/                # Coordenadas geográficas
├── nasapower/                  # Dados NASA POWER
├── produtor/                   # Produtores rurais
├── propriedade/                # Propriedades rurais
├── satveg/                     # Dados SatVeg (satélite)
├── talhao/                     # Talhões (parcelas de terra)
├── telefone/                   # Telefones dos produtores
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

- Usa **Bean Validation** (`@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Pattern`, etc.)
- Contém método **`toEntity(...)`** que recebe as entidades de FK já resolvidas.
- **Não contém** lógica de montagem de APIs externas (esses conversores ficam em métodos privados no Service, ex: `buildApiQuery()`). Mantendo o DTO puramente como validador de entrada.
- FKs são representadas como `Long idRelacao` (ID puro, não a entidade).

#### Response (saída)

- **Imutável** — apenas dados serializáveis
- Contém **`static fromEntity(Entidade)`** que extrai IDs das relações
- **Implementa HATEOAS:** Possui o método **`toEntityModel()`** que retorna um `EntityModel<T>` preenchido com links estáticos (ex: `withSelfRel()`), garantindo isolamento da lógica HATEOAS dentro do próprio DTO sem vazar entidades.

### Services

- **`@Service` + `@RequiredArgsConstructor`** — injeção via construtor final (Lombok)
- Transações gerenciadas por **`@Transactional`** nos métodos `create`, `update` e `delete` para garantir Data Integrity e First-Level Cache efficiency.
- **Paginação:** métodos `findAll` retornam `Page<Response>` com `Pageable`
- **Delete otimizado:** `repository.delete(entity)` (após validar se a entidade existe), poupando 1 query SELECT extra do Spring Data.
- **APIs Externas:** Injeção de `OpenFeign Clients` (ex: `SatVegClient`, `NasaPowerClient`) executados durante o fluxo de `create` em caso de enriquecimento de dados dinâmico. Tratamento de exceção em integrações não devem parar o fluxo local (usar fallback).

### Integrações (OpenFeign)

- Clientes criados como `interface` com `@FeignClient`.
- Para métodos GET complexos, utiliza-se `@SpringQueryMap Map<String, Object>` para construir os Query Params de forma limpa.
- Respostas da API são mapeadas em Records exclusivos da API (ex: `NasaPowerDataResponse`) aninhados para ignorar campos indesejados do JSON externo.

### Controllers

- **`@RestController` + `@RequestMapping("/api/<recurso>")` + `@RequiredArgsConstructor`**
- **`@Valid`** em todos os `@RequestBody`
- **`Pageable`** no endpoint de listagem (GET sem ID)
- **HTTP Status:** `201 CREATED` no POST, `204 NO_CONTENT` no DELETE, `200 OK` nos demais
- **Retornos HATEOAS:** Todos os retornos que antes eram DTOs diretos agora são **envelopados em `EntityModel<T>` ou `Page<EntityModel<T>>`**, acionando o `.toEntityModel()` do Response.
- **CORS:** Liberado globalmente via classe de infraestrutura `CorsConfig` implementando `WebMvcConfigurer`.

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
  ├──── (1:N) NasaPower ───── (1:N) DadoTemporal
  └──── (1:N) SatVeg ──────── (1:N) DadoTemporal
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
| `nasapower` | `talhao_id_talhao` | `talhao.id_talhao` |
| `satveg` | `talhao_id_talhao` | `talhao.id_talhao` |
| `alerta_agricola` | `talhao_id_talhao` | `talhao.id_talhao` |
| `dado_temporal` | `nasapower_id_nasapower` | `nasapower.id_nasapower` |
| `dado_temporal` | `satveg_id_satveg` | `satveg.id_satveg` |

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
4. **Tratamento de Exceções:** Centralizado em `GlobalExceptionHandler`. Utilizar `@ResponseStatus` caso criar exceptions customizadas (como `ResourceNotFoundException`).
5. **Transações:** `@Transactional` é vital em operações de escrita nos Services.
6. **Desacoplamento API:** As chamadas OpenFeign utilizam conversão direta em `NasaPowerDataResponse` e `SatVegDataResponse`, ignorando JSON inflado.

---

## 🧪 Para rodar

```bash
./mvnw spring-boot:run
```

- **API:** `http://localhost:8080`
- **Swagger:** `http://localhost:8080/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/h2-console`
