package com.rentgames.web;

import com.rentgames.model.Aluguel;
import com.rentgames.model.Jogo;
import com.rentgames.service.AluguelService;
import com.rentgames.service.JogoService;
import com.rentgames.service.exception.ValidacaoException;
import com.rentgames.web.dto.AluguelRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF05, RF06, RF07, RF08, RF09, RF10. */
@RestController
@RequestMapping("/api/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;
    private final JogoService jogoService;

    public AluguelController(AluguelService aluguelService, JogoService jogoService) {
        this.aluguelService = aluguelService;
        this.jogoService = jogoService;
    }

    @GetMapping
    public List<Aluguel> listarTodos() {
        return aluguelService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aluguel registrar(@RequestBody AluguelRequest request) {
        Jogo jogo = jogoService.buscarPorId(parseId(request.idJogo(), "Selecione um jogo."))
                .orElseThrow(() -> new ValidacaoException("Jogo nao encontrado."));
        return aluguelService.registrar(request.nomeCliente(), request.contato(), request.cpf(),
                request.endereco(), jogo, request.dias());
    }

    @PostMapping("/{id}/devolucao")
    public Aluguel registrarDevolucao(@PathVariable int id) {
        Aluguel aluguel = aluguelService.buscarPorId(id)
                .orElseThrow(() -> new ValidacaoException("Aluguel nao encontrado."));
        aluguelService.registrarDevolucao(aluguel);
        return aluguelService.buscarPorId(id).orElse(aluguel);
    }

    private int parseId(String texto, String mensagemSeVazio) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException(mensagemSeVazio);
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Jogo invalido.");
        }
    }
}
