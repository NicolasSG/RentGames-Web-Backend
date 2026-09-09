package com.rentgames.repository.jdbc;

import com.rentgames.config.DatabaseConfig;
import com.rentgames.model.Jogo;
import com.rentgames.repository.JogoRepository;
import com.rentgames.service.exception.RegraNegocioException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Unica responsabilidade: traduzir operacoes de Jogo em SQL/JDBC.
 * Nao conhece Swing nem regras de negocio (equivalente refatorado de dao.JogoDAO).
 * Bean gerenciado pelo Spring (Etapa 9).
 */
@Repository
public class JogoRepositoryJdbc implements JogoRepository {

    @Override
    public Jogo salvar(Jogo jogo) {
        String sql = "INSERT INTO jogos (nome, ano, plataforma, valor_aluguel, dias_aluguel, estoque) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, jogo.getNome());
            stmt.setInt(2, jogo.getAno());
            stmt.setString(3, jogo.getPlataforma());
            stmt.setDouble(4, jogo.getValorAluguel());
            stmt.setInt(5, jogo.getDiasAluguelPadrao());
            stmt.setInt(6, jogo.getQuantidadeEstoque());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return new Jogo(keys.getInt(1), jogo.getNome(), jogo.getAno(), jogo.getPlataforma(),
                        jogo.getValorAluguel(), jogo.getDiasAluguelPadrao(), jogo.getQuantidadeEstoque());
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao cadastrar jogo", e);
        }
    }

    @Override
    public List<Jogo> listarTodos() {
        List<Jogo> lista = new ArrayList<>();
        String sql = "SELECT * FROM jogos";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar jogos", e);
        }
        return lista;
    }

    @Override
    public Optional<Jogo> buscarPorId(int id) {
        String sql = "SELECT * FROM jogos WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao buscar jogo " + id, e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM jogos WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // A FK alugueis.id_jogo -> jogos.id impede excluir um jogo que ja
            // tem historico de aluguel. Sem este catch especifico, a excecao
            // subia como RepositoryException generica e virava 500 sem
            // explicar o motivo real (issues#3, Etapa 9).
            throw new RegraNegocioException("Nao e possivel excluir um jogo que ja possui alugueis registrados.");
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao excluir jogo " + id, e);
        }
    }

    @Override
    public void decrementarEstoque(int idJogo) {
        atualizarEstoque(idJogo, "estoque - 1");
    }

    @Override
    public void incrementarEstoque(int idJogo) {
        atualizarEstoque(idJogo, "estoque + 1");
    }

    private void atualizarEstoque(int idJogo, String expressao) {
        String sql = "UPDATE jogos SET estoque = " + expressao + " WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idJogo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao atualizar estoque do jogo " + idJogo, e);
        }
    }

    private Jogo map(ResultSet rs) throws SQLException {
        return new Jogo(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getInt("ano"),
            rs.getString("plataforma"),
            rs.getDouble("valor_aluguel"),
            rs.getInt("dias_aluguel"),
            rs.getInt("estoque")
        );
    }
}
