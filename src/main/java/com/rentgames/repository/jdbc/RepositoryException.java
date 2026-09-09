package com.rentgames.repository.jdbc;

/**
 * Envolve falhas de acesso a dados (SQLException) em uma unchecked exception.
 * Antes, cada DAO decidia sozinho como reagir a um erro de SQL (imprimir no
 * console, abrir um JOptionPane, devolver null ou uma lista vazia - de forma
 * inconsistente entre as classes). Agora o erro sempre propaga para a camada
 * de service/view decidir como apresentar o problema ao usuario.
 */
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
