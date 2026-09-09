# Evidencias de Versionamento - RentGames Web Backend (Etapa 9)

## 1. Repositorio

- **Repositorio (publico):** https://github.com/NicolasSG/RentGames-Web-Backend
- **Owner:** NicolasSG
- **Branch principal:** `main`

Este e um **novo repositorio**, criado especificamente para esta etapa (requisito da
atividade: "Aplique versionamento ao projeto, criando novo repositorio"), diferente dos
repositorios das etapas anteriores do PI:

| Etapa | Conteudo | Repositorio |
|---|---|---|
| 6 e 7 | Sistema desktop refatorado (SOLID) + testes JUnit | https://github.com/NicolasSG/RentGames-refatorado |
| 8 | Front-end estatico (HTML/CSS/JS) | https://github.com/NicolasSG/RentGames-Web |
| **9** | **Back-end Spring Boot REST + integracao com o front-end da Etapa 8** | **https://github.com/NicolasSG/RentGames-Web-Backend** |

(O repositorio original do sistema desktop, antes da refatoracao, e
https://github.com/NicolasSG/RentGames.)

## 2. Historico de commits

```
$ git log --oneline
97816f3 Corrige badge de aluguel atrasado (verde -> vermelho) e adiciona README
8dd8681 Corrige exclusao de jogo com historico de alugueis (500 -> 409)
41cdb09 Integra o front-end da Etapa 8 com a API REST (Etapa 9)
6f026ca Adiciona bootstrap automatico do primeiro usuario ADMIN
a7725e6 Corrige login invalido para devolver 401 em vez de 409
8ef3034 Cria back-end Spring Boot REST do RentGames (Etapa 9)
```

| Commit | Descricao | Link |
|---|---|---|
| `8ef3034` | Cria o back-end Spring Boot REST (model/repository/service reaproveitados da Etapa 6 como beans do Spring, controllers REST, GlobalExceptionHandler) | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/8ef3034) |
| `a7725e6` | Fixes #1 - login invalido devolve 401 | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/a7725e6) |
| `6f026ca` | Fixes #2 - bootstrap automatico do primeiro ADMIN | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/6f026ca) |
| `41cdb09` | Integra o front-end estatico da Etapa 8 (paginas + `store.js` reescrito para consumir a API REST) | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/41cdb09) |
| `8dd8681` | Fixes #3 - exclusao de jogo com historico (FK) devolve 409 em vez de 500 | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/8dd8681) |
| `97816f3` | Fixes #4 - badge de aluguel atrasado; adiciona README | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/97816f3) |

Cada commit de correcao de bug referencia a Issue correspondente (`Fixes #N`), o que fecha
a issue automaticamente no GitHub e cria um link direto entre o problema relatado e a
mudanca de codigo que o resolveu - ver `docs/EVIDENCIAS-TESTES.md` para o detalhamento de
cada bug.

## 3. Issues (bugtracking) vinculadas ao historico

https://github.com/NicolasSG/RentGames-Web-Backend/issues?q=is%3Aissue+is%3Aclosed

- [#1](https://github.com/NicolasSG/RentGames-Web-Backend/issues/1) - fechada por `a7725e6`
- [#2](https://github.com/NicolasSG/RentGames-Web-Backend/issues/2) - fechada por `6f026ca`
- [#3](https://github.com/NicolasSG/RentGames-Web-Backend/issues/3) - fechada por `8dd8681`
- [#4](https://github.com/NicolasSG/RentGames-Web-Backend/issues/4) - fechada por `97816f3`

## 4. O que esta versionado

- Codigo-fonte completo do back-end (`src/main/java`), incluindo o codigo reaproveitado
  da Etapa 6 (model/repository/service) e o codigo novo desta etapa (`web/*`,
  `config/AdminBootstrap`).
- Testes JUnit da Etapa 7 (`src/test/java`), sem alteracao.
- Front-end estatico da Etapa 8 (`src/main/resources/static`), com `store.js` adaptado
  para a API REST.
- `sql/schema.sql`, `pom.xml`, `README.md`, esta pasta `docs/`.
- **Nao versionado (`.gitignore`):** `target/` (build), `db.properties` (credenciais reais
  do MySQL local - so `db.properties.example` vai para o repositorio).
