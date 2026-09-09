package com.rentgames.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testa as regras de calculo de data de devolucao e de atraso (RF10 - ver
 * docs/PROJETO-DE-SOFTWARE-DESKTOP.md), que ja eram metodos proprios do
 * modelo desde a Etapa 6 (Aluguel.isAtrasado / getStatusDescricao).
 */
class AluguelTest {

    private final Jogo jogo = new Jogo(1, "Elden Ring", 2022, "PC", 20.0, 7, 5);

    @Test
    void dataDevolucaoEhDataInicioMaisDiasDeAluguel() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        Aluguel aluguel = new Aluguel(1, "Maria", "119999", "", "Rua Um", jogo, 7, inicio);

        assertEquals(LocalDate.of(2026, 1, 8), aluguel.getDataDevolucao());
    }

    @Test
    void aluguelRecenteEComDataFuturaNaoEstaAtrasado() {
        Aluguel aluguel = new Aluguel(1, "Maria", "119999", "", "Rua Um", jogo, 7, LocalDate.now());

        assertFalse(aluguel.isAtrasado());
        assertEquals("Ativo", aluguel.getStatusDescricao());
    }

    @Test
    void aluguelComDataDevolucaoNoPassadoENaoDevolvidoEstaAtrasado() {
        Aluguel aluguel = new Aluguel(1, "Maria", "119999", "", "Rua Um", jogo, 1,
                LocalDate.now().minusDays(10));

        assertTrue(aluguel.isAtrasado());
        assertEquals("Atrasado!", aluguel.getStatusDescricao());
    }

    @Test
    void aluguelDevolvidoNuncaEstaAtrasadoMesmoForaDoPrazo() {
        Aluguel aluguel = new Aluguel(1, "Maria", "119999", "", "Rua Um", jogo, 1,
                LocalDate.now().minusDays(10));
        aluguel.setDevolvido(true);

        assertFalse(aluguel.isAtrasado());
        assertEquals("Devolvido", aluguel.getStatusDescricao());
    }
}
