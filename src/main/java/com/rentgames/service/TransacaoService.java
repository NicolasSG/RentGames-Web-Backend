package com.rentgames.service;

import com.rentgames.model.Transacao;
import com.rentgames.repository.TransacaoRepository;
import com.rentgames.service.exception.ValidacaoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Regras de negocio do caixa, extraidas de view.CaixaPanel
 * (Extract Class / Move Method). Bean gerenciado pelo Spring (Etapa 9).
 */
@Service
public class TransacaoService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TransacaoRepository transacaoRepository;

    public TransacaoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public Transacao registrarManual(String descricao, String valorTexto, String data, Transacao.Tipo tipo) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new ValidacaoException("Informe uma descricao para a transacao.");
        }
        double valor;
        try {
            valor = Double.parseDouble(valorTexto.trim().replace(",", "."));
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacaoException("Valor invalido.");
        }
        return registrar(new Transacao(0, descricao.trim(), tipo, valor, data));
    }

    public Transacao registrar(Transacao transacao) {
        return transacaoRepository.salvar(transacao);
    }

    public Transacao registrarEntradaDeAluguel(String descricao, double valor) {
        return registrar(new Transacao(0, descricao, Transacao.Tipo.ENTRADA, valor, LocalDate.now().format(FORMATO_DATA)));
    }

    public List<Transacao> listarTodas() {
        return transacaoRepository.listarTodas();
    }

    /**
     * Saldo atual do caixa: busca todas as transacoes e aplica a regra de
     * calculo em {@link #calcularSaldo(List)}.
     */
    public double calcularSaldo() {
        return calcularSaldo(transacaoRepository.listarTodas());
    }

    /**
     * Regra de calculo do saldo (entradas - saidas), separada em um metodo
     * proprio, puro e sem acesso a banco, exatamente para poder ser testada
     * com JUnit sem depender de MySQL (ver
     * com.rentgames.service.TransacaoServiceTest, Etapa 7 do PI).
     */
    public double calcularSaldo(List<Transacao> transacoes) {
        double entradas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.Tipo.ENTRADA)
                .mapToDouble(Transacao::getValor)
                .sum();
        double saidas = transacoes.stream()
                .filter(t -> t.getTipo() == Transacao.Tipo.SAIDA)
                .mapToDouble(Transacao::getValor)
                .sum();
        return entradas - saidas;
    }
}
