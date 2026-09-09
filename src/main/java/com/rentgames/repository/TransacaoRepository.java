package com.rentgames.repository;

import com.rentgames.model.Transacao;
import java.util.List;

public interface TransacaoRepository {
    /** Devolve a entidade persistida (com o id gerado pelo banco). Ajuste da Etapa 9 para a API REST. */
    Transacao salvar(Transacao transacao);
    List<Transacao> listarTodas();
}
