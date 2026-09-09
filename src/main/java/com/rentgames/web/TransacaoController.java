package com.rentgames.web;

import com.rentgames.model.Transacao;
import com.rentgames.service.TransacaoService;
import com.rentgames.service.exception.ValidacaoException;
import com.rentgames.web.dto.SaldoResponse;
import com.rentgames.web.dto.TransacaoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF11, RF12. */
@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public List<Transacao> listarTodas() {
        return transacaoService.listarTodas();
    }

    @GetMapping("/saldo")
    public SaldoResponse saldo() {
        return new SaldoResponse(transacaoService.calcularSaldo());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transacao registrar(@RequestBody TransacaoRequest request) {
        Transacao.Tipo tipo = parseTipo(request.tipo());
        return transacaoService.registrarManual(request.descricao(), request.valor(), request.data(), tipo);
    }

    private Transacao.Tipo parseTipo(String texto) {
        try {
            return Transacao.Tipo.valueOf(String.valueOf(texto).trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidacaoException("Tipo de transacao invalido.");
        }
    }
}
