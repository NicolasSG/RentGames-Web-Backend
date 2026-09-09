package com.rentgames.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testa a regra de hash/conferencia de senha (RNF05 - ver
 * docs/PROJETO-DE-SOFTWARE-DESKTOP.md), que substituiu o armazenamento em
 * texto puro do sistema desktop original.
 */
class PasswordHasherTest {

    @Test
    void senhaCorretaEhConfirmadaContraOHash() {
        String hash = PasswordHasher.hash("minhaSenha123");
        assertTrue(PasswordHasher.confere("minhaSenha123", hash));
    }

    @Test
    void senhaIncorretaNaoEhConfirmada() {
        String hash = PasswordHasher.hash("minhaSenha123");
        assertFalse(PasswordHasher.confere("senhaErrada", hash));
    }

    @Test
    void hashNuncaEIgualASenhaEmTextoPuro() {
        String senha = "minhaSenha123";
        assertNotEquals(senha, PasswordHasher.hash(senha));
    }

    @Test
    void mesmaSenhaGeraHashesDiferentesPorCausaDoSalt() {
        String hash1 = PasswordHasher.hash("minhaSenha123");
        String hash2 = PasswordHasher.hash("minhaSenha123");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void hashInvalidoOuNuloNuncaConfereComSucesso() {
        assertFalse(PasswordHasher.confere("qualquerSenha", null));
        assertFalse(PasswordHasher.confere("qualquerSenha", "hash-sem-separador"));
    }
}
