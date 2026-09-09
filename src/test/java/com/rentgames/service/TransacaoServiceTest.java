package com.rentgames.service;

import com.rentgames.model.Transacao;
import com.rentgames.test.InMemoryTransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testa a regra de calculo do saldo do caixa (RF12 - ver
 * docs/PROJETO-DE-SOFTWARE-DESKTOP.md), isolada em
 * TransacaoService.calcularSaldo(List) exatamente para nao depender de MySQL
 * (funcionalidade de calculo simples, sem acesso a banco - Etapa 7 do PI).
 */
class TransacaoServiceTest {

    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        transacaoService = new TransacaoService(new InMemoryTransacaoRepository());
    }

    @Test
    void saldoDeListaVaziaEhZero() {
        assertEquals(0.0, transacaoService.calcularSaldo(List.of()));
    }

    @Test
    void saldoSomaApenasEntradas() {
        List<Transacao> transacoes = List.of(
                new Transacao(1, "Aluguel A", Transacao.Tipo.ENTRADA, 15.0, "01/01/2026"),
                new Transacao(2, "Aluguel B", Transacao.Tipo.ENTRADA, 25.0, "02/01/2026")
        );
        assertEquals(40.0, transacaoService.calcularSaldo(transacoes));
    }

    @Test
    void saldoSubtraiSaidas() {
        List<Transacao> transacoes = List.of(
                new Transacao(1, "Abertura de caixa", Transacao.Tipo.ENTRADA, 200.0, "01/01/2026"),
                new Transacao(2, "Compra de material", Transacao.Tipo.SAIDA, 50.0, "01/01/2026"),
                new Transacao(3, "Conta de luz", Transacao.Tipo.SAIDA, 30.0, "02/01/2026")
        );
        assertEquals(120.0, transacaoService.calcularSaldo(transacoes));
    }

    @Test
    void saldoPodeFicarNegativoQuandoSaidasSuperamEntradas() {
        List<Transacao> transacoes = List.of(
                new Transacao(1, "Abertura de caixa", Transacao.Tipo.ENTRADA, 10.0, "01/01/2026"),
                new Transacao(2, "Manutencao", Transacao.Tipo.SAIDA, 50.0, "01/01/2026")
        );
        assertEquals(-40.0, transacaoService.calcularSaldo(transacoes));
    }

    @Test
    void calcularSaldoSemArgumentosUsaAsTransacoesRegistradas() {
        transacaoService.registrarManual("Abertura de caixa", "200,00", "01/01/2026", Transacao.Tipo.ENTRADA);
        transacaoService.registrarManual("Compra de material", "50,00", "01/01/2026", Transacao.Tipo.SAIDA);

        assertEquals(150.0, transacaoService.calcularSaldo());
    }
}
