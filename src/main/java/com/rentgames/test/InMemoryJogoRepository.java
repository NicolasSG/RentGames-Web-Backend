package com.rentgames.test;

import com.rentgames.model.Jogo;
import com.rentgames.repository.JogoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacao de JogoRepository que guarda tudo em memoria, sem MySQL.
 * Usada apenas por com.rentgames.test.SelfTest para provar, na pratica, o
 * beneficio do Dependency Inversion Principle: AluguelService e JogoService
 * funcionam identicamente com esta implementacao ou com JogoRepositoryJdbc,
 * pois ambas dependem apenas da interface JogoRepository.
 */
public class InMemoryJogoRepository implements JogoRepository {

    private final List<Jogo> jogos = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Jogo salvar(Jogo jogo) {
        Jogo salvo = new Jogo(proximoId++, jogo.getNome(), jogo.getAno(), jogo.getPlataforma(),
                jogo.getValorAluguel(), jogo.getDiasAluguelPadrao(), jogo.getQuantidadeEstoque());
        jogos.add(salvo);
        return salvo;
    }

    @Override
    public List<Jogo> listarTodos() {
        return new ArrayList<>(jogos);
    }

    @Override
    public Optional<Jogo> buscarPorId(int id) {
        return jogos.stream().filter(j -> j.getId() == id).findFirst();
    }

    @Override
    public void excluir(int id) {
        jogos.removeIf(j -> j.getId() == id);
    }

    @Override
    public void decrementarEstoque(int idJogo) {
        buscarPorId(idJogo).ifPresent(j -> j.setQuantidadeEstoque(j.getQuantidadeEstoque() - 1));
    }

    @Override
    public void incrementarEstoque(int idJogo) {
        buscarPorId(idJogo).ifPresent(j -> j.setQuantidadeEstoque(j.getQuantidadeEstoque() + 1));
    }
}
