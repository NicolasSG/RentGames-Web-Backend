package com.rentgames.repository;

import com.rentgames.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    /** Devolve a entidade persistida (com o id gerado pelo banco). Ajuste da Etapa 9 para a API REST. */
    Usuario salvar(Usuario usuario);
    List<Usuario> listarTodos();
    void excluir(int id);
    Optional<Usuario> buscarPorEmail(String email);
}
