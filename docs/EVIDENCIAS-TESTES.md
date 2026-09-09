# Evidencias de Testes e Bugtracking - RentGames Web Backend (Etapa 9)

Repositorio: https://github.com/NicolasSG/RentGames-Web-Backend

## 1. Testes automatizados (JUnit, Etapa 7)

Os 22 testes JUnit criados na Etapa 7 foram trazidos para este projeto sem alteracao
(cobrem `model`/`service`, que nao mudaram) e continuam passando depois de toda a
integracao com Spring Boot e MySQL:

```
mvn test
```

```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 -- com.rentgames.model.AluguelTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- com.rentgames.model.JogoTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- com.rentgames.security.PasswordHasherTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- com.rentgames.service.AluguelServiceTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- com.rentgames.service.TransacaoServiceTest

Total: 22 testes, 0 falhas, 0 erros.
```

## 2. Bugtracking (GitHub Issues)

Ferramenta usada: **GitHub Issues** do proprio repositorio
(https://github.com/NicolasSG/RentGames-Web-Backend/issues). Cada bug encontrado durante
a execucao do plano de testes manuais (secao 3) foi registrado como Issue, corrigido em um
commit que referencia a issue (`Fixes #N`), e fechado.

| # | Titulo | Encontrado em | Commit de correcao | Status |
|---|---|---|---|---|
| [#1](https://github.com/NicolasSG/RentGames-Web-Backend/issues/1) | Login invalido devolvia HTTP 409 em vez de 401 | MT-02 | [13d534e](https://github.com/NicolasSG/RentGames-Web-Backend/commit/13d534e) | Fechada |
| [#2](https://github.com/NicolasSG/RentGames-Web-Backend/issues/2) | Impossivel criar o primeiro usuario ADMIN (bootstrap ausente) | Preparacao do MT-16 | [0dda304](https://github.com/NicolasSG/RentGames-Web-Backend/commit/0dda304) | Fechada |
| [#3](https://github.com/NicolasSG/RentGames-Web-Backend/issues/3) | Excluir jogo com historico de alugueis derrubava 500 generico (violacao de FK) | MT-06 | [3a20b64](https://github.com/NicolasSG/RentGames-Web-Backend/commit/3a20b64) | Fechada |
| [#4](https://github.com/NicolasSG/RentGames-Web-Backend/issues/4) | Aluguel atrasado aparecia com badge verde em vez de vermelho | MT-13 | [88b4d04](https://github.com/NicolasSG/RentGames-Web-Backend/commit/88b4d04) | Fechada |

### 2.1 Resumo de cada bug

**#1 - Login invalido devolvia 409, nao 401.** `GlobalExceptionHandler` mapeava toda
`RegraNegocioException` para 409 Conflict (correto para "e-mail ja cadastrado", errado
para "senha incorreta", que e 401 Unauthorized). `AuthController.login` passou a tratar
esse caso especificamente.

**#2 - Nao havia como criar o primeiro ADMIN.** `POST /api/auth/registrar` so cria
FUNCIONARIO; `POST /api/usuarios` (que permite escolher ADMIN) exige um ADMIN ja
existente para autorizar - ninguem conseguia sair desse impasse. Corrigido com
`AdminBootstrap` (`ApplicationRunner`): cria um ADMIN padrao na subida da aplicacao se
nenhum ainda existir.

**#3 - Excluir jogo com aluguel no historico quebrava com 500.** A tabela `alugueis` tem
`FOREIGN KEY (id_jogo) REFERENCES jogos(id)`; excluir um jogo referenciado violava essa
FK, e a excecao subia como erro generico de banco (500, sem explicacao). Corrigido
capturando especificamente `SQLIntegrityConstraintViolationException` em
`JogoRepositoryJdbc.excluir` e traduzindo para uma `RegraNegocioException` com mensagem
clara (409). Aproveitado tambem para adicionar log server-side de erros de repositorio,
que antes ficavam completamente mudos no console.

**#4 - Badge de aluguel atrasado aparecia verde.** `Aluguel.getStatusDescricao()` (Etapa 6)
devolve o texto `"Atrasado!"` (com exclamacao); o front-end comparava com `"Atrasado"`
(sem exclamacao), entao a comparacao nunca era verdadeira e o badge caia no caso padrao
(verde/sucesso). Corrigido usando os campos booleanos `devolvido`/`atrasado` que a API ja
devolve prontos, em vez de comparar texto.

## 3. Execucao do plano de testes manuais (Etapa 7) contra a aplicacao real

Executado com a aplicacao rodando (`mvn spring-boot:run`) e MySQL real (`rentgames_db`).
IDs conforme `docs/PLANO-DE-TESTES.md` da Etapa 7 (RentGames-refatorado).

| ID | Requisito | Resultado | Observacao |
|---|---|---|---|
| MT-01 | RF01 - login com sucesso | OK | Login como ADMIN redireciona para `dashboard.html` com dados reais do banco |
| MT-02 | RF01 - login com senha incorreta | OK (apos correcao) | Ver bug #1 - agora devolve 401 com mensagem clara |
| MT-03 | RF02 - bloquear acesso sem login | OK | `jogos.html` sem sessao redireciona para `login.html` |
| MT-04 | RF03 - cadastrar jogo valido | OK | Jogo aparece na tabela imediatamente apos cadastro |
| MT-05 | RF03 - cadastro de jogo invalido | OK | Nome vazio -> 400 "Preencha Nome, Ano e Plataforma." |
| MT-06 | RF03 - excluir jogo | OK (apos correcao) | Ver bug #3 - excluir jogo sem historico funciona (204); com historico, 409 com mensagem clara |
| MT-07 | RF04 - cadastrar cliente | OK | |
| MT-08 | RF05/RF06 - aluguel com estoque disponivel | OK | Estoque decrementado no banco, refletido na tela |
| MT-09 | RF05 - bloquear jogo sem estoque na lista de disponiveis | OK | `GET /api/jogos/disponiveis` nao retorna jogo com estoque 0 |
| MT-10 | RF07 - aluguel gera transacao no caixa | OK | Transacao ENTRADA "Aluguel - <jogo>" aparece e o saldo soma o valor |
| MT-11 | RF08 - registrar devolucao | OK | Estoque volta a incrementar, status muda para "Devolvido" |
| MT-12 | RF09 - bloquear devolucao duplicada | OK | Segunda tentativa devolve 409 "Este aluguel ja foi devolvido." |
| MT-13 | RF10 - indicar aluguel em atraso | OK (apos correcao) | Ver bug #4 - badge agora fica vermelho para "Atrasado!" |
| MT-14 | RF11 - registrar transacao manual | OK | |
| MT-15 | RF12 - saldo do caixa | OK | Saldo = soma entradas - soma saidas, testado com valores reais |
| MT-16 | RF13 - ADMIN cadastra usuario | OK (apos correcao) | Ver bug #2 - precisou do bootstrap para existir o primeiro ADMIN |
| MT-17 | RF13 - menu Usuarios oculto para FUNCIONARIO | OK | Escondido no front-end **e** bloqueado no servidor (`403`, ver `UsuarioController.exigirAdmin` - reforco novo desta etapa) |
| MT-18 | RF14 - rejeitar valor numerico invalido | OK | Ano = "abc" -> 400 "Ano invalido." |

**18/18 casos do plano de testes manuais executados com sucesso** (4 deles so depois das
correcoes registradas na secao 2).

## 4. Como reproduzir esta bateria de testes

```bash
# 1. Testes unitarios (nao precisa de banco)
mvn test

# 2. Aplicacao completa (precisa de MySQL com o schema aplicado - ver README.md)
mvn spring-boot:run

# 3. Exemplos de teste manual via curl (equivalentes aos MT-* acima)
curl -s -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"admin@rentgames.com","senha":"123456"}'

curl -s http://localhost:8080/api/jogos
curl -s http://localhost:8080/api/transacoes/saldo
```
