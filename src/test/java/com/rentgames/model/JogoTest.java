package com.rentgames.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Testa a regra "jogo tem estoque disponivel" (RF05). */
class JogoTest {

    @Test
    void jogoComEstoquePositivoTemDisponibilidade() {
        Jogo jogo = new Jogo(1, "The Witcher 3", 2015, "PC", 15.0, 7, 1);
        assertTrue(jogo.temEstoqueDisponivel());
    }

    @Test
    void jogoComEstoqueZeradoNaoTemDisponibilidade() {
        Jogo jogo = new Jogo(1, "The Witcher 3", 2015, "PC", 15.0, 7, 0);
        assertFalse(jogo.temEstoqueDisponivel());
    }

    @Test
    void estoqueNegativoTambemNaoTemDisponibilidade() {
        Jogo jogo = new Jogo(1, "The Witcher 3", 2015, "PC", 15.0, 7, -1);
        assertFalse(jogo.temEstoqueDisponivel());
    }
}
