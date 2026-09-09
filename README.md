# RentGames Web Backend (Etapa 9 do PI)

Back-end Spring Boot (REST) do RentGames: aplica banco de dados via JDBC/MySQL,
reaproveita quase sem alteracoes o `model`/`repository`/`service` da
[Etapa 6](https://github.com/NicolasSG/RentGames-refatorado) e os testes JUnit da
Etapa 7, e serve/alimenta o front-end estatico da
[Etapa 8](https://github.com/NicolasSG/RentGames-Web) via API REST.

## Estrutura

```
src/main/java/com/rentgames/
├── RentGamesApplication.java   # @SpringBootApplication (substitui Main.java do desktop)
├── model/                      # Jogo, Cliente, Aluguel, Transacao, Usuario (Etapa 6, sem alteracao)
├── repository/ (+ jdbc/)       # interfaces + JDBC (Etapa 6) - agora @Repository do Spring
├── service/ (+ exception/)     # regras de negocio (Etapa 6) - agora @Service do Spring
├── security/PasswordHasher     # hash de senha (Etapa 6, sem alteracao)
├── config/
│   ├── DatabaseConfig          # le db.properties/env vars (Etapa 6, sem alteracao)
│   └── AdminBootstrap          # cria o 1o usuario ADMIN se nao existir (novo - issues#2)
├── web/                        # controllers REST + GlobalExceptionHandler (novo)
│   └── dto/                    # records de requisicao/resposta (novo)
└── test/                       # InMemory*Repository + SelfTest (Etapa 6/7, sem alteracao)

src/main/resources/static/      # front-end da Etapa 8 (HTML/CSS/JS), servido pelo Spring Boot
src/test/java/com/rentgames/    # testes JUnit da Etapa 7 (sem alteracao)
```

## Como rodar

1. MySQL 8 rodando localmente, com o schema aplicado:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```
2. Copie `src/main/resources/db.properties.example` para `db.properties` e preencha
   (recomendado: um usuario dedicado, nao root - ver comentario no arquivo).
3. Suba a aplicacao:
   ```bash
   mvn spring-boot:run
   ```
4. Abra **http://localhost:8080** no navegador (redireciona para `login.html`).
   Primeiro acesso: **admin@rentgames.com** / **123456** (criado automaticamente por
   `AdminBootstrap` se nenhum ADMIN existir ainda - ver console na subida).

## Testes

```bash
mvn test    # 22 testes JUnit (Etapa 7), sem depender de banco
```

Evidencia completa dos testes manuais executados contra a API real (MySQL rodando) e
o registro de bugtracking (GitHub Issues) estao em
[`docs/EVIDENCIAS-TESTES.md`](docs/EVIDENCIAS-TESTES.md).
Evidencia do versionamento em
[`docs/EVIDENCIAS-VERSIONAMENTO.md`](docs/EVIDENCIAS-VERSIONAMENTO.md).

## API REST

| Metodo | Rota | Descricao |
|---|---|---|
| POST | `/api/auth/login` | RF01 - autenticar |
| POST | `/api/auth/registrar` | Auto-registro (sempre FUNCIONARIO) |
| GET/POST | `/api/jogos` | RF03 - listar / cadastrar |
| GET | `/api/jogos/disponiveis` | RF05 - so com estoque > 0 |
| DELETE | `/api/jogos/{id}` | RF03 - excluir |
| GET/POST | `/api/clientes` | RF04 |
| DELETE | `/api/clientes/{id}` | RF04 |
| GET/POST | `/api/alugueis` | RF05, RF06, RF07 |
| POST | `/api/alugueis/{id}/devolucao` | RF08, RF09 |
| GET/POST | `/api/transacoes` | RF11 |
| GET | `/api/transacoes/saldo` | RF12 |
| GET/POST | `/api/usuarios` | RF13 - **somente ADMIN** (cabecalho `X-User-Email`) |
| DELETE | `/api/usuarios/{id}` | RF13 - somente ADMIN |

## Limitacoes conhecidas (proximas etapas)

- Sem Spring Security: a autorizacao de `/api/usuarios` e uma checagem manual via
  cabecalho `X-User-Email` (ver `UsuarioController.exigirAdmin`), nao um mecanismo de
  autenticacao completo (token/sessao no servidor).
- `DatabaseConfig` abre uma `Connection` por operacao (sem connection pool) - adequado
  para o volume de uso desta etapa, mas um `DataSource`/HikariCP seria o proximo passo
  natural para producao.
