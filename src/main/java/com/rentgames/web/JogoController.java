package com.rentgames.web;

import com.rentgames.model.Jogo;
import com.rentgames.service.JogoService;
import com.rentgames.web.dto.JogoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF03, RF05, RF14. */
@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @GetMapping
    public List<Jogo> listarTodos() {
        return jogoService.listarTodos();
    }

    @GetMapping("/disponiveis")
    public List<Jogo> listarDisponiveis() {
        return jogoService.listarDisponiveisParaAluguel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Jogo cadastrar(@RequestBody JogoRequest request) {
        return jogoService.cadastrar(request.nome(), request.ano(), request.plataforma(),
                request.valorAluguel(), request.quantidadeEstoque(), request.diasAluguelPadrao());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable int id) {
        jogoService.excluir(id);
    }
}
