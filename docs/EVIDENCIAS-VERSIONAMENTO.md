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
3f365f0 Adiciona documentos de evidencia de testes/bugtracking e versionamento
88b4d04 Corrige badge de aluguel atrasado (verde -> vermelho) e adiciona README
3a20b64 Corrige exclusao de jogo com historico de alugueis (500 -> 409)
a15e221 Integra o front-end da Etapa 8 com a API REST (Etapa 9)
0dda304 Adiciona bootstrap automatico do primeiro usuario ADMIN
13d534e Corrige login invalido para devolver 401 em vez de 409
bf3fdb9 Cria back-end Spring Boot REST do RentGames (Etapa 9)
```

| Commit | Descricao | Link |
|---|---|---|
| `bf3fdb9` | Cria o back-end Spring Boot REST (model/repository/service reaproveitados da Etapa 6 como beans do Spring, controllers REST, GlobalExceptionHandler) | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/bf3fdb9) |
| `13d534e` | Fixes #1 - login invalido devolve 401 | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/13d534e) |
| `0dda304` | Fixes #2 - bootstrap automatico do primeiro ADMIN | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/0dda304) |
| `a15e221` | Integra o front-end estatico da Etapa 8 (paginas + `store.js` reescrito para consumir a API REST) | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/a15e221) |
| `3a20b64` | Fixes #3 - exclusao de jogo com historico (FK) devolve 409 em vez de 500 | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/3a20b64) |
| `88b4d04` | Fixes #4 - badge de aluguel atrasado; adiciona README | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/88b4d04) |
| `3f365f0` | Adiciona os dois documentos de evidencia desta etapa | [ver](https://github.com/NicolasSG/RentGames-Web-Backend/commit/3f365f0) |

Cada commit de correcao de bug referencia a Issue correspondente (`Fixes #N`), o que fecha
a issue automaticamente no GitHub e cria um link direto entre o problema relatado e a
mudanca de codigo que o resolveu - ver `docs/EVIDENCIAS-TESTES.md` para o detalhamento de
cada bug.

> **Nota sobre os hashes:** os 7 commits acima foram reescritos uma vez (mesmo conteudo,
> mensagens sem a linha de coautoria de ferramenta de IA que a Claude Code inclui por
> padrao) e o branch `main` foi atualizado com `git push --force`, a pedido explicito do
> autor. As issues do GitHub permaneceram fechadas normalmente (o estado de uma issue nao
> depende do commit continuar existindo com o mesmo hash).

## 3. Issues (bugtracking) vinculadas ao historico

https://github.com/NicolasSG/RentGames-Web-Backend/issues?q=is%3Aissue+is%3Aclosed

- [#1](https://github.com/NicolasSG/RentGames-Web-Backend/issues/1) - fechada por `13d534e`
- [#2](https://github.com/NicolasSG/RentGames-Web-Backend/issues/2) - fechada por `0dda304`
- [#3](https://github.com/NicolasSG/RentGames-Web-Backend/issues/3) - fechada por `3a20b64`
- [#4](https://github.com/NicolasSG/RentGames-Web-Backend/issues/4) - fechada por `88b4d04`

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
