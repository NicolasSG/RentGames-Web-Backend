package com.rentgames.test;

import com.rentgames.model.Transacao;
import com.rentgames.repository.TransacaoRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTransacaoRepository implements TransacaoRepository {

    private final List<Transacao> transacoes = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Transacao salvar(Transacao transacao) {
        Transacao salva = new Transacao(proximoId++, transacao.getDescricao(), transacao.getTipo(),
                transacao.getValor(), transacao.getData());
        transacoes.add(salva);
        return salva;
    }

    @Override
    public List<Transacao> listarTodas() {
        return new ArrayList<>(transacoes);
    }
}
