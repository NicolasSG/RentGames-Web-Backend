package com.rentgames.test;

import com.rentgames.model.Aluguel;
import com.rentgames.repository.AluguelRepository;
import com.rentgames.repository.JogoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryAluguelRepository implements AluguelRepository {

    private final List<Aluguel> alugueis = new ArrayList<>();
    private final JogoRepository jogoRepository;
    private int proximoId = 1;

    public InMemoryAluguelRepository(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    @Override
    public Aluguel salvar(Aluguel aluguel) {
        Aluguel salvo = new Aluguel(proximoId++, aluguel.getNomeCliente(), aluguel.getContato(),
                aluguel.getCpf(), aluguel.getEndereco(), aluguel.getJogo(), aluguel.getDiasAluguel(),
                aluguel.getDataInicio());
        alugueis.add(salvo);
        jogoRepository.decrementarEstoque(aluguel.getJogo().getId());
        return salvo;
    }

    @Override
    public List<Aluguel> listarTodos() {
        return new ArrayList<>(alugueis);
    }

    @Override
    public Optional<Aluguel> buscarPorId(int id) {
        return alugueis.stream().filter(a -> a.getId() == id).findFirst();
    }

    @Override
    public void registrarDevolucao(int idAluguel, int idJogo) {
        alugueis.stream()
                .filter(a -> a.getId() == idAluguel)
                .findFirst()
                .ifPresent(a -> a.setDevolvido(true));
        jogoRepository.incrementarEstoque(idJogo);
    }
}
