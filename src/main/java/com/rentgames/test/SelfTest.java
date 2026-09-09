package com.rentgames.test;

import com.rentgames.model.Aluguel;
import com.rentgames.model.Jogo;
import com.rentgames.model.Transacao;
import com.rentgames.model.Usuario;
import com.rentgames.service.AluguelService;
import com.rentgames.service.AppContext;
import com.rentgames.service.ClienteService;
import com.rentgames.service.JogoService;
import com.rentgames.service.TransacaoService;
import com.rentgames.service.UsuarioService;
import com.rentgames.service.exception.RegraNegocioException;
import com.rentgames.service.exception.ValidacaoException;

import java.util.List;

/**
 * Bateria de testes manuais executada via main(), como pedido no roteiro da
 * atividade ("implemente testes no metodo main() para se certificar de que
 * tudo esta funcionando").
 *
 * Em vez de se conectar a um MySQL de verdade, este teste monta um AppContext
 * com repositorios EM MEMORIA (com.rentgames.test.InMemory*Repository) -
 * a mesma classe de service (JogoService, AluguelService, etc.) roda sem
 * nenhuma alteracao, porque toda a camada de service depende apenas das
 * interfaces em com.rentgames.repository (Dependency Inversion Principle).
 * Isso e a prova pratica de que a arquitetura permite testar as regras de
 * negocio sem precisar de banco de dados nem da interface Swing.
 *
 * Uso: mvn compile exec:java -Dexec.mainClass=com.rentgames.test.SelfTest
 */
public final class SelfTest {

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        InMemoryJogoRepository jogoRepository = new InMemoryJogoRepository();
        AppContext ctx = new AppContext(
                jogoRepository,
                new InMemoryClienteRepository(),
                new InMemoryAluguelRepository(jogoRepository),
                new InMemoryTransacaoRepository(),
                new InMemoryUsuarioRepository()
        );

        testarCadastroDeJogo(ctx.jogoService());
        testarCadastroDeCliente(ctx.clienteService());
        testarFluxoDeAluguelComBaixaDeEstoque(ctx);
        testarBloqueiaAluguelSemEstoque(ctx);
        testarDevolucaoDuplicadaEhBloqueada(ctx);
        testarCaixaCalculaSaldo(ctx.transacaoService());
        testarCadastroEAutenticacaoDeUsuario(ctx.usuarioService());
        testarLoginComSenhaErradaFalha(ctx.usuarioService());
        testarValidacaoDeCamposObrigatorios(ctx.jogoService());

        System.out.println();
        System.out.println(falhas == 0
                ? "TODOS OS TESTES PASSARAM (" + total + "/" + total + ")"
                : (total - falhas) + "/" + total + " testes passaram, " + falhas + " falharam");

        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testarCadastroDeJogo(JogoService jogoService) {
        jogoService.cadastrar("The Witcher 3", "2015", "PC", "15,00", "5", "7");
        List<Jogo> jogos = jogoService.listarTodos();
        checar("cadastro de jogo aparece na listagem", jogos.size() == 1
                && jogos.get(0).getNome().equals("The Witcher 3")
                && jogos.get(0).getQuantidadeEstoque() == 5);
    }

    private static void testarCadastroDeCliente(ClienteService clienteService) {
        clienteService.cadastrar("Ana Clara", "11999990000", "12345678900", "Rua Dois");
        checar("cadastro de cliente aparece na listagem", clienteService.listarTodos().size() == 1);
    }

    private static void testarFluxoDeAluguelComBaixaDeEstoque(AppContext ctx) {
        ctx.jogoService().cadastrar("Elden Ring", "2022", "PC", "20,00", "2", "7");
        Jogo eldenRing = ctx.jogoService().listarTodos().stream()
                .filter(j -> j.getNome().equals("Elden Ring")).findFirst().orElseThrow();

        Aluguel aluguel = ctx.aluguelService().registrar("Maria", "11988887777", "", "Rua Cinco", eldenRing, "7");

        Jogo eldenRingAtualizado = ctx.jogoService().listarTodos().stream()
                .filter(j -> j.getId() == eldenRing.getId()).findFirst().orElseThrow();
        checar("aluguel decrementa o estoque do jogo", eldenRingAtualizado.getQuantidadeEstoque() == 1);

        double saldoAposAluguel = ctx.transacaoService().calcularSaldo();
        checar("aluguel gera transacao de entrada no caixa", saldoAposAluguel == 20.0);

        ctx.aluguelService().registrarDevolucao(aluguel);
        Jogo eldenRingPosDevolucao = ctx.jogoService().listarTodos().stream()
                .filter(j -> j.getId() == eldenRing.getId()).findFirst().orElseThrow();
        checar("devolucao repoe o estoque do jogo", eldenRingPosDevolucao.getQuantidadeEstoque() == 2);
    }

    private static void testarBloqueiaAluguelSemEstoque(AppContext ctx) {
        ctx.jogoService().cadastrar("God of War", "2022", "PS5", "25,00", "0", "7");
        Jogo semEstoque = ctx.jogoService().listarTodos().stream()
                .filter(j -> j.getNome().equals("God of War")).findFirst().orElseThrow();

        boolean bloqueou = false;
        try {
            ctx.aluguelService().registrar("Joao", "119999", "", "Rua Um", semEstoque, "7");
        } catch (RegraNegocioException e) {
            bloqueou = true;
        }
        checar("aluguel de jogo sem estoque e bloqueado", bloqueou);
    }

    private static void testarDevolucaoDuplicadaEhBloqueada(AppContext ctx) {
        ctx.jogoService().cadastrar("Hades", "2020", "PC", "10,00", "1", "7");
        Jogo hades = ctx.jogoService().listarTodos().stream()
                .filter(j -> j.getNome().equals("Hades")).findFirst().orElseThrow();
        Aluguel aluguel = ctx.aluguelService().registrar("Carlos", "119999", "", "Rua Tres", hades, "7");
        ctx.aluguelService().registrarDevolucao(aluguel);

        boolean bloqueou = false;
        try {
            ctx.aluguelService().registrarDevolucao(aluguel);
        } catch (RegraNegocioException e) {
            bloqueou = true;
        }
        checar("devolver o mesmo aluguel duas vezes e bloqueado", bloqueou);
    }

    private static void testarCaixaCalculaSaldo(TransacaoService transacaoService) {
        double saldoAntes = transacaoService.calcularSaldo();
        transacaoService.registrarManual("Abertura de caixa", "200,00", "01/01/2026", Transacao.Tipo.ENTRADA);
        transacaoService.registrarManual("Compra de material", "50,00", "01/01/2026", Transacao.Tipo.SAIDA);
        checar("saldo do caixa soma entradas e subtrai saidas",
                transacaoService.calcularSaldo() == saldoAntes + 150.0);
    }

    private static void testarCadastroEAutenticacaoDeUsuario(UsuarioService usuarioService) {
        usuarioService.autoRegistrar("admin@rentgames.com", "senhaForte123");
        Usuario autenticado = usuarioService.autenticar("admin@rentgames.com", "senhaForte123");
        checar("login com senha correta autentica o usuario", autenticado != null
                && autenticado.getEmail().equals("admin@rentgames.com"));
        checar("senha nao e armazenada em texto puro", !autenticado.getSenhaHash().equals("senhaForte123"));
    }

    private static void testarLoginComSenhaErradaFalha(UsuarioService usuarioService) {
        boolean bloqueou = false;
        try {
            usuarioService.autenticar("admin@rentgames.com", "senhaErrada");
        } catch (RegraNegocioException e) {
            bloqueou = true;
        }
        checar("login com senha errada e rejeitado", bloqueou);
    }

    private static void testarValidacaoDeCamposObrigatorios(JogoService jogoService) {
        boolean bloqueou = false;
        try {
            jogoService.cadastrar("", "2024", "PC", "10", "1", "7");
        } catch (ValidacaoException e) {
            bloqueou = true;
        }
        checar("cadastro de jogo sem nome e rejeitado", bloqueou);
    }

    private static void checar(String descricao, boolean condicao) {
        total++;
        if (condicao) {
            System.out.println("[OK]    " + descricao);
        } else {
            falhas++;
            System.out.println("[FALHOU] " + descricao);
        }
    }
}
