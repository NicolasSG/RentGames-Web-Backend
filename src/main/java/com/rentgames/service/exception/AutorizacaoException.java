package com.rentgames.service.exception;

/**
 * Usuario autenticado mas sem permissao para a acao (ex.: FUNCIONARIO
 * tentando gerenciar usuarios). Nova na Etapa 9: a Etapa 8 so escondia o
 * menu "Usuarios" no front-end para quem nao era ADMIN - isso e so
 * cosmetico, qualquer pessoa podia chamar a API direto. Esta excecao da
 * origem a uma checagem de verdade no servidor (ver UsuarioController).
 */
public class AutorizacaoException extends RuntimeException {
    public AutorizacaoException(String message) {
        super(message);
    }
}
