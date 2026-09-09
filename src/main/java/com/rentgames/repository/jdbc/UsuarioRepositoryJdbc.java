package com.rentgames.repository.jdbc;

import com.rentgames.config.DatabaseConfig;
import com.rentgames.model.Usuario;
import com.rentgames.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Equivalente refatorado de dao.UsuarioDAO. Ao contrario da versao original,
 * nao autentica comparando "senha" via SQL (isso deixou de ser possivel pois
 * a senha agora e armazenada com hash+salt, ver PasswordHasher); a
 * autenticacao passou a ser responsabilidade da camada de service, que busca
 * o usuario por e-mail e confere o hash.
 * Bean gerenciado pelo Spring (Etapa 9).
 */
@Repository
public class UsuarioRepositoryJdbc implements UsuarioRepository {

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, contato, cpf, endereco, perfil) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getContato());
            stmt.setString(5, usuario.getCpf());
            stmt.setString(6, usuario.getEndereco());
            stmt.setString(7, usuario.getNivelPermissao().name());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return new Usuario(keys.getInt(1), usuario.getNomeCompleto(), usuario.getEmail(),
                        usuario.getSenhaHash(), usuario.getContato(), usuario.getCpf(), usuario.getEndereco(),
                        usuario.getNivelPermissao());
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao cadastrar usuario", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar usuarios", e);
        }
        return usuarios;
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao excluir usuario " + id, e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao buscar usuario por email", e);
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("senha"),
            rs.getString("contato"),
            rs.getString("cpf"),
            rs.getString("endereco"),
            Usuario.NivelPermissao.valueOf(rs.getString("perfil").toUpperCase())
        );
    }
}
