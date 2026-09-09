package com.rentgames.test;

import com.rentgames.model.Cliente;
import com.rentgames.repository.ClienteRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryClienteRepository implements ClienteRepository {

    private final List<Cliente> clientes = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Cliente salvar(Cliente cliente) {
        Cliente salvo = new Cliente(proximoId++, cliente.getNomeCompleto(), cliente.getContato(),
                cliente.getCpf(), cliente.getEndereco());
        clientes.add(salvo);
        return salvo;
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }

    @Override
    public void excluir(int id) {
        clientes.removeIf(c -> c.getId() == id);
    }
}
