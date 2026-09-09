package com.rentgames.repository;

import com.rentgames.model.Jogo;
import java.util.List;
import java.util.Optional;

/**
 * Abstracao de persistencia para Jogo. As camadas de servico dependem desta
 * interface (Dependency Inversion Principle), nao da implementacao JDBC
 * concreta - permitindo trocar o mecanismo de armazenamento (ex.: um fake em
 * memoria usado nos testes de com.rentgames.test.SelfTest) sem alterar
 * nenhuma regra de negocio.
 */
public interface JogoRepository {
    /** Devolve a entidade persistida (com o id gerado pelo banco). Ajuste da Etapa 9 para a API REST. */
    Jogo salvar(Jogo jogo);
    List<Jogo> listarTodos();
    Optional<Jogo> buscarPorId(int id);
    void excluir(int id);
    void decrementarEstoque(int idJogo);
    void incrementarEstoque(int idJogo);
}
