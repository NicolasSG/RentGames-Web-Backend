package com.rentgames.web.dto;

/** tipo esperado: "ENTRADA" ou "SAIDA" (com.rentgames.model.Transacao.Tipo). */
public record TransacaoRequest(String descricao, String valor, String data, String tipo) {
}
