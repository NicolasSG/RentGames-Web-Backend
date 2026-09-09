package com.rentgames.repository.jdbc;

import com.rentgames.config.DatabaseConfig;
import com.rentgames.model.Aluguel;
import com.rentgames.model.Jogo;
import com.rentgames.repository.AluguelRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Equivalente refatorado de dao.AluguelDAO. Bean gerenciado pelo Spring (Etapa 9). */
@Repository
public class AluguelRepositoryJdbc implements AluguelRepository {

    @Override
    public Aluguel salvar(Aluguel aluguel) {
        String sqlAluguel = "INSERT INTO alugueis (nome_cliente, contato, cpf, endereco, id_jogo, dias_aluguel, data_inicio, devolvido) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlEstoque = "UPDATE jogos SET estoque = estoque - 1 WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idGerado;
                try (PreparedStatement stmt = conn.prepareStatement(sqlAluguel, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, aluguel.getNomeCliente());
                    stmt.setString(2, aluguel.getContato());
                    stmt.setString(3, aluguel.getCpf());
                    stmt.setString(4, aluguel.getEndereco());
                    stmt.setInt(5, aluguel.getJogo().getId());
                    stmt.setInt(6, aluguel.getDiasAluguel());
                    stmt.setDate(7, Date.valueOf(aluguel.getDataInicio()));
                    stmt.setBoolean(8, false);
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        keys.next();
                        idGerado = keys.getInt(1);
                    }
                }
                try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque)) {
                    stmt.setInt(1, aluguel.getJogo().getId());
                    stmt.executeUpdate();
                }
                conn.commit();
                return new Aluguel(idGerado, aluguel.getNomeCliente(), aluguel.getContato(), aluguel.getCpf(),
                        aluguel.getEndereco(), aluguel.getJogo(), aluguel.getDiasAluguel(), aluguel.getDataInicio());
            } catch (SQLException e) {
                conn.rollback();
                throw new RepositoryException("Erro ao registrar aluguel", e);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao registrar aluguel", e);
        }
    }

    private static final String SQL_BASE =
            "SELECT a.*, j.nome as nome_jogo, j.valor_aluguel FROM alugueis a "
            + "JOIN jogos j ON a.id_jogo = j.id ";

    @Override
    public List<Aluguel> listarTodos() {
        List<Aluguel> lista = new ArrayList<>();
        String sql = SQL_BASE + "ORDER BY a.id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar alugueis", e);
        }
        return lista;
    }

    @Override
    public java.util.Optional<Aluguel> buscarPorId(int id) {
        String sql = SQL_BASE + "WHERE a.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? java.util.Optional.of(mapRow(rs)) : java.util.Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao buscar aluguel " + id, e);
        }
    }

    private Aluguel mapRow(ResultSet rs) throws SQLException {
        Jogo jogo = new Jogo(rs.getInt("id_jogo"), rs.getString("nome_jogo"), 0, "",
                rs.getDouble("valor_aluguel"), 0, 0);
        Aluguel aluguel = new Aluguel(
            rs.getInt("id"),
            rs.getString("nome_cliente"),
            rs.getString("contato"),
            rs.getString("cpf"),
            rs.getString("endereco"),
            jogo,
            rs.getInt("dias_aluguel"),
            rs.getDate("data_inicio").toLocalDate()
        );
        aluguel.setDevolvido(rs.getBoolean("devolvido"));
        return aluguel;
    }

    @Override
    public void registrarDevolucao(int idAluguel, int idJogo) {
        String sqlStatus = "UPDATE alugueis SET devolvido = true WHERE id = ?";
        String sqlEstoque = "UPDATE jogos SET estoque = estoque + 1 WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(sqlStatus)) {
                    stmt.setInt(1, idAluguel);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque)) {
                    stmt.setInt(1, idJogo);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RepositoryException("Erro ao registrar devolucao", e);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao registrar devolucao", e);
        }
    }
}
