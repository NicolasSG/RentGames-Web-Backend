package com.rentgames.repository;

import com.rentgames.model.Aluguel;
import java.util.List;
import java.util.Optional;

public interface AluguelRepository {
    /**
     * Grava o aluguel e da baixa no estoque do jogo em uma unica transacao.
     * Devolve a entidade persistida (com o id gerado pelo banco), pois o
     * objeto recebido como parametro sempre chega com id = 0.
     */
    Aluguel salvar(Aluguel aluguel);
    List<Aluguel> listarTodos();
    /** Adicionado na Etapa 9 para a rota REST de devolucao (POST /api/alugueis/{id}/devolucao). */
    Optional<Aluguel> buscarPorId(int id);
    /** Marca o aluguel como devolvido e repoe o estoque do jogo em uma unica transacao. */
    void registrarDevolucao(int idAluguel, int idJogo);
}
