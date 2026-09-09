package com.rentgames.web.dto;

import com.rentgames.model.Usuario;

/**
 * Nunca devolvemos Usuario direto pela API (o campo senhaHash vazaria no
 * JSON). Este DTO expoe so o que a interface precisa saber sobre um usuario.
 */
public record UsuarioResponse(int id, String nomeCompleto, String email, String nivelPermissao) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNomeCompleto(), usuario.getEmail(),
                usuario.getNivelPermissao().name());
    }
}
