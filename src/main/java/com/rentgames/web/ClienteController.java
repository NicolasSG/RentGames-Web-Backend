package com.rentgames.web;

import com.rentgames.model.Cliente;
import com.rentgames.service.ClienteService;
import com.rentgames.web.dto.ClienteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF04. */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente cadastrar(@RequestBody ClienteRequest request) {
        return clienteService.cadastrar(request.nomeCompleto(), request.contato(), request.cpf(), request.endereco());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable int id) {
        clienteService.excluir(id);
    }
}
