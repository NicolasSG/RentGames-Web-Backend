package com.rentgames.service;

import com.rentgames.model.Aluguel;
import com.rentgames.model.Jogo;
import com.rentgames.service.exception.RegraNegocioException;
import com.rentgames.service.exception.ValidacaoException;
import com.rentgames.test.InMemoryAluguelRepository;
import com.rentgames.test.InMemoryJogoRepository;
import com.rentgames.test.InMemoryTransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testa as regras de negocio de aluguel/devolucao (RF05, RF06, RF07, RF08,
 * RF09) sem acesso a banco de dados, usando os mesmos repositorios em
 * memoria reaproveitados de com.rentgames.test (Etapa 6).
 */
class AluguelServiceTest {

    private JogoService jogoService;
    private AluguelService aluguelService;

    @BeforeEach
    void setUp() {
        InMemoryJogoRepository jogoRepository = new InMemoryJogoRepository();
        jogoService = new JogoService(jogoRepository);
        TransacaoService transacaoService = new TransacaoService(new InMemoryTransacaoRepository());
        aluguelService = new AluguelService(new InMemoryAluguelRepository(jogoRepository), transacaoService);
    }

    private Jogo cadastrarJogoComEstoque(int estoque) {
        jogoService.cadastrar("Elden Ring", "2022", "PC", "20,00", String.valueOf(estoque), "7");
        return jogoService.listarTodos().get(0);
    }

    @Test
    void registrarAluguelDecrementaOEstoqueDoJogo() {
        Jogo jogo = cadastrarJogoComEstoque(2);

        aluguelService.registrar("Maria", "119999", "", "Rua Um", jogo, "7");

        Jogo jogoAtualizado = jogoService.listarTodos().get(0);
        assertEquals(1, jogoAtualizado.getQuantidadeEstoque());
    }

    @Test
    void registrarAluguelDeJogoSemEstoqueLancaRegraDeNegocio() {
        Jogo jogo = cadastrarJogoComEstoque(0);

        assertThrows(RegraNegocioException.class,
                () -> aluguelService.registrar("Maria", "119999", "", "Rua Um", jogo, "7"));
    }

    @Test
    void registrarAluguelSemNomeDeClienteLancaValidacao() {
        Jogo jogo = cadastrarJogoComEstoque(1);

        assertThrows(ValidacaoException.class,
                () -> aluguelService.registrar("", "119999", "", "Rua Um", jogo, "7"));
    }

    @Test
    void devolucaoRepoeOEstoqueDoJogo() {
        Jogo jogo = cadastrarJogoComEstoque(1);
        Aluguel aluguel = aluguelService.registrar("Maria", "119999", "", "Rua Um", jogo, "7");

        aluguelService.registrarDevolucao(aluguel);

        Jogo jogoAtualizado = jogoService.listarTodos().get(0);
        assertEquals(1, jogoAtualizado.getQuantidadeEstoque());
    }

    @Test
    void devolverOMesmoAluguelDuasVezesLancaRegraDeNegocio() {
        Jogo jogo = cadastrarJogoComEstoque(1);
        Aluguel aluguel = aluguelService.registrar("Maria", "119999", "", "Rua Um", jogo, "7");
        aluguelService.registrarDevolucao(aluguel);

        assertThrows(RegraNegocioException.class, () -> aluguelService.registrarDevolucao(aluguel));
    }
}
