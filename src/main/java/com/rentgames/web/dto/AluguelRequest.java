package com.rentgames.web.dto;

public record AluguelRequest(String nomeCliente, String contato, String cpf, String endereco,
                              String idJogo, String dias) {
}
