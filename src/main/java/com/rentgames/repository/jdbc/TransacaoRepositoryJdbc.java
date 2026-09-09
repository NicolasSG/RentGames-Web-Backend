package com.rentgames.repository.jdbc;

import com.rentgames.config.DatabaseConfig;
import com.rentgames.model.Transacao;
import com.rentgames.repository.TransacaoRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Equivalente refatorado de dao.TransacaoDAO.
 * O metodo "salvar(Transacao)" que existia no DAO original preparava um
 * PreparedStatement mas nunca definia os parametros nem chamava executeUpdate()
 * - ou seja, nunca gravava nada. Era codigo morto/quebrado duplicando
 * "cadastrar(Transacao)" e foi removido nesta refatoracao.
 *
 * O calculo de saldo (antes um "SELECT ... SUM(valor) ... GROUP BY tipo" aqui
 * mesmo) foi movido para TransacaoService.calcularSaldo(List), um metodo puro
 * e testavel com JUnit sem depender do banco (ver Etapa 7 do PI).
 * Bean gerenciado pelo Spring (Etapa 9).
 */
@Repository
public class TransacaoRepositoryJdbc implements TransacaoRepository {

    @Override
    public Transacao salvar(Transacao transacao) {
        String sql = "INSERT INTO transacoes (descricao, tipo, valor, data) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transacao.getDescricao());
            stmt.setString(2, transacao.getTipo().name());
            stmt.setDouble(3, transacao.getValor());
            stmt.setString(4, transacao.getData());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                return new Transacao(keys.getInt(1), transacao.getDescricao(), transacao.getTipo(),
                        transacao.getValor(), transacao.getData());
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao cadastrar transacao", e);
        }
    }

    @Override
    public List<Transacao> listarTodas() {
        List<Transacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM transacoes ORDER BY id DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Transacao(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    Transacao.Tipo.valueOf(rs.getString("tipo")),
                    rs.getDouble("valor"),
                    rs.getString("data")
                ));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erro ao listar transacoes", e);
        }
        return lista;
    }
}
