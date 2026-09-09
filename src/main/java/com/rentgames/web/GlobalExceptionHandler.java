package com.rentgames.web;

import com.rentgames.repository.jdbc.RepositoryException;
import com.rentgames.service.exception.AutorizacaoException;
import com.rentgames.service.exception.RegraNegocioException;
import com.rentgames.service.exception.ValidacaoException;
import com.rentgames.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Unica responsabilidade: traduzir cada excecao de dominio (Etapas 6/7) para
 * o status HTTP correto, em um so lugar. Sem isso, cada controller teria que
 * repetir os mesmos try/catch - o mesmo tipo de duplicacao que a Etapa 6 ja
 * havia eliminado da camada Swing.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErrorResponse> tratarValidacao(ValidacaoException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> tratarRegraNegocio(RegraNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AutorizacaoException.class)
    public ResponseEntity<ErrorResponse> tratarAutorizacao(AutorizacaoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<ErrorResponse> tratarRepositorio(RepositoryException e) {
        // Nao devolve e.getMessage()/causa para o cliente: poderia vazar detalhe de SQL/schema.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Erro ao acessar o banco de dados. Tente novamente mais tarde."));
    }
}
