package com.rentgames.web.dto;

/** nivelPermissao esperado: "ADMIN" ou "FUNCIONARIO". */
public record UsuarioRequest(String nomeCompleto, String email, String contato, String cpf,
                              String endereco, String nivelPermissao) {
}
