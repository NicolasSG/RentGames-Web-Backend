package com.rentgames.service;

import com.rentgames.model.Cliente;
import com.rentgames.repository.ClienteRepository;
import com.rentgames.service.exception.ValidacaoException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio de Cliente, extraidas de view.ClientesPanel
 * (Extract Class / Move Method). Bean gerenciado pelo Spring (Etapa 9).
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente cadastrar(String nome, String contato, String cpf, String endereco) {
        if (isBlank(nome) || isBlank(contato) || isBlank(endereco)) {
            throw new ValidacaoException("Preencha os campos obrigatorios (nome, contato e endereco).");
        }
        return clienteRepository.salvar(new Cliente(0, nome.trim(), contato.trim(),
                cpf == null ? "" : cpf.trim(), endereco.trim()));
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public void excluir(int id) {
        clienteRepository.excluir(id);
    }

    private boolean isBlank(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
