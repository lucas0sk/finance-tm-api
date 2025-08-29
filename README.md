# Finance API — Desafio Tokio Marine

API REST para agendamento e liquidação de transferências financeiras com autenticação JWT, cálculo de taxas por data, idempotência e extrato paginado.

## Tecnologias
- Java 11 (conforme exigência do desafio)
- Spring Boot 2.7.18
- Spring MVC, Spring Data JPA, Spring Security (JWT)
- H2 Database (em memória)
- Lombok
- Springdoc OpenAPI (Swagger UI)

## Decisões Arquiteturais
- **Domain-driven**: entidades `User`, `Account`, `Transfer` representam o núcleo do domínio.
- **Idempotência**: garantida via `requestId` único em `Transfer`.
- **Concorrência**: uso de `@Version` em `Account` para optimistic locking ao debitar/creditar.
- **Segurança**: JWT stateless, com roles `USER` e `ADMIN`. O back-end sempre valida `fromAccount` contra o usuário autenticado (exceto ADMIN).
- **Liquidação**: implementada via job `@Scheduled` que processa transferências vencidas (status `PENDING`).
- **Camadas**:
  - `api/` → controllers, DTOs, handlers
  - `service/` → regras de negócio
  - `repository/` → Spring Data JPA
  - `domain/` → entidades e enums
  - `config/` → segurança, scheduling
  - `job/` → jobs agendados

## Como Rodar

```bash
# Clonar o repositório
git clone https://github.com/lucas0sk/finance-api.git
cd finance-api

# Build do projeto
mvn clean package

# Rodar aplicação
mvn spring-boot:run
```

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:finance_tm_db`
  - Usuário: definido em `application.yml`
  - Senha: (vazia)

## Seed de dados
Ao iniciar, o sistema cria:
- **admin / admin123** → `ROLE_ADMIN`

Cada usuário possui conta vinculada automaticamente.

## Endpoints principais

### Autenticação
- `POST /auth/login` → retorna JWT + número da conta

### Conta
- `GET /api/accounts/me` → saldo e número da conta do usuário autenticado

### Transferências
- `POST /api/transfers/schedule` → agenda uma transferência
- `GET /api/transfers/me` → extrato do usuário autenticado (paginado, com filtros)
- `GET /api/transfers` (ADMIN) → lista geral de transferências

### Admin
- `POST /api/admin/settlements/run-today` → liquida transferências pendentes até a data atual

## Regras de Taxa
| Dias até a transferência | Taxa Fixa (R$) | Percentual (%) |
|---|---:|---:|
| 0 | 3,00 | 2,5% |
| 1–10 | 12,00 | 0,0% |
| 11–20 | 0,00 | 8,2% |
| 21–30 | 0,00 | 6,9% |
| 31–40 | 0,00 | 4,7% |
| 41–50 | 0,00 | 1,7% |

Caso não haja taxa aplicável, o sistema retorna erro e não permite o agendamento.

## Idempotência
- Garantida por `requestId` (UUID) em `Transfer`.
- Requisições repetidas com mesmo `requestId` retornam o mesmo registro (sem duplicar débito).

## Paginação & Ordenação
- Padrão do Spring Data: `page`, `size`, `sort` (exemplo: `?page=0&size=20&sort=createdAt,desc`).

## Liquidação
- Transferências criadas com status `PENDING`.
- Job `@Scheduled` processa as vencidas (até `transferDate`):
  - Debita **totalAmount** (valor + taxa) da origem.
  - Credita **amount** ao destino.
  - Marca como `SUCCESS` ou `FAILED` (saldo insuficiente).

## Versões
- Java: 11
- Spring Boot: 2.7.18
- Maven Compiler Plugin: 3.10.1
- Lombok: 1.18.x
- Springdoc OpenAPI: 1.7.0

