package com.rentgames.service.exception;

/** Dados de entrada invalidos (campo obrigatorio vazio, numero mal formatado, etc). */
public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String message) {
        super(message);
    }
}
