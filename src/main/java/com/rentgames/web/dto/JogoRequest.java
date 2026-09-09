package com.rentgames.web.dto;

/** Mesmos campos de texto que JogoService.cadastrar(...) ja validava na Etapa 6. */
public record JogoRequest(String nome, String ano, String plataforma, String valorAluguel,
                           String quantidadeEstoque, String diasAluguelPadrao) {
}
