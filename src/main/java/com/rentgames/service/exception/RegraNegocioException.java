package com.rentgames.service.exception;

/** Violacao de uma regra de negocio (ex.: estoque insuficiente, aluguel ja devolvido). */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String message) {
        super(message);
    }
}
