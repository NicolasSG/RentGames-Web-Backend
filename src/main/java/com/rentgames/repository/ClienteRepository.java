package com.rentgames.repository;

import com.rentgames.model.Cliente;
import java.util.List;

public interface ClienteRepository {
    /** Devolve a entidade persistida (com o id gerado pelo banco). Ajuste da Etapa 9 para a API REST. */
    Cliente salvar(Cliente cliente);
    List<Cliente> listarTodos();
    void excluir(int id);
}
