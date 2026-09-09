package com.rentgames.test;

import com.rentgames.model.Usuario;
import com.rentgames.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUsuarioRepository implements UsuarioRepository {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Usuario salvar(Usuario usuario) {
        Usuario salvo = new Usuario(proximoId++, usuario.getNomeCompleto(), usuario.getEmail(),
                usuario.getSenhaHash(), usuario.getContato(), usuario.getCpf(), usuario.getEndereco(),
                usuario.getNivelPermissao());
        usuarios.add(salvo);
        return salvo;
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public void excluir(int id) {
        usuarios.removeIf(u -> u.getId() == id);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }
}
