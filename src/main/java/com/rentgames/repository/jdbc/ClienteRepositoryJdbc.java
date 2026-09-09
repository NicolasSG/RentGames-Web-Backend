package com.rentgames.repository.jdbc;

import com.rentgames.config.DatabaseConfig;
import com.rentgames.model.Cliente;
import com.rentgames.repository.ClienteRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Equivalente refatorado de dao.ClienteDAO. Bean gerenciado pelo Spring (Etapa 9). */
@Repository
public class ClienteRepositoryJdbc implements ClienteRepository {

    @Override
    public Cliente salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNomeCompleto());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getContato());
            stmt.setString(4, "Nao informado");
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return new Cliente(keys.getInt(1), cliente.getNomeCompleto(), cliente.getContato(),
                        cliente.getCpf(), cliente.getEndereco());
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao cadastrar cliente", e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("telefone"),
                    rs.getString("cpf"),
                    "Endereco nao cadastrado"
                ));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar clientes", e);
        }
        return clientes;
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao excluir cliente " + id, e);
        }
    }
}
