package com.rentgames.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Unica responsabilidade: gerar e conferir hashes de senha (SHA-256 + salt).
 * Substitui o armazenamento e a comparacao de senha em texto puro que existiam
 * em dao.UsuarioDAO / dao.Conexao na versao desktop original.
 */
public final class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    private PasswordHasher() { }

    public static String hash(String senhaTextoPuro) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] digest = digest(senhaTextoPuro, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(digest);
    }

    public static boolean confere(String senhaTextoPuro, String hashArmazenado) {
        if (hashArmazenado == null || !hashArmazenado.contains(":")) return false;
        String[] partes = hashArmazenado.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] digestEsperado = Base64.getDecoder().decode(partes[1]);
        byte[] digestCalculado = digest(senhaTextoPuro, salt);
        return MessageDigest.isEqual(digestEsperado, digestCalculado);
    }

    private static byte[] digest(String senha, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(senha.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponivel: " + ALGORITHM, e);
        }
    }
}
