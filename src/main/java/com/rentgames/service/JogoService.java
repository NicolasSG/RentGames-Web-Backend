package com.rentgames.service;

import com.rentgames.model.Jogo;
import com.rentgames.repository.JogoRepository;
import com.rentgames.service.exception.ValidacaoException;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Regras de negocio de Jogo. Antes, a validacao de campos e a conversao de
 * texto para numero (Integer.parseInt/Double.parseDouble) ficavam dentro de
 * view.JogosPanel.btnCadastrarActionPerformed, misturadas com codigo de
 * interface grafica. Extraidas para ca (Extract Class / Extract Method) para
 * que possam ser testadas sem depender do Swing (ver com.rentgames.test.SelfTest).
 * Bean gerenciado pelo Spring (Etapa 9) e tambem usado, sem framework nenhum,
 * por AppContext (Etapa 6) e SelfTest.
 */
@Service
public class JogoService {

    private static final int DIAS_ALUGUEL_PADRAO = 7;

    private final JogoRepository jogoRepository;

    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    public Jogo cadastrar(String nome, String anoTexto, String plataforma,
                           String valorTexto, String estoqueTexto, String diasTexto) {
        if (isBlank(nome) || isBlank(anoTexto) || isBlank(plataforma)) {
            throw new ValidacaoException("Preencha Nome, Ano e Plataforma.");
        }

        int ano = parseInt(anoTexto, "Ano invalido.");
        double valor = isBlank(valorTexto) ? 0 : parseDouble(valorTexto, "Valor de aluguel invalido.");
        int estoque = isBlank(estoqueTexto) ? 0 : parseInt(estoqueTexto, "Estoque invalido.");
        int dias = isBlank(diasTexto) ? DIAS_ALUGUEL_PADRAO : parseInt(diasTexto, "Dias de aluguel invalido.");

        return jogoRepository.salvar(new Jogo(0, nome.trim(), ano, plataforma.trim(), valor, dias, estoque));
    }

    public List<Jogo> listarTodos() {
        return jogoRepository.listarTodos();
    }

    public Optional<Jogo> buscarPorId(int id) {
        return jogoRepository.buscarPorId(id);
    }

    public List<Jogo> listarDisponiveisParaAluguel() {
        return jogoRepository.listarTodos().stream()
                .filter(Jogo::temEstoqueDisponivel)
                .toList();
    }

    public void excluir(int id) {
        jogoRepository.excluir(id);
    }

    private boolean isBlank(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private int parseInt(String texto, String mensagemErro) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException(mensagemErro);
        }
    }

    private double parseDouble(String texto, String mensagemErro) {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValidacaoException(mensagemErro);
        }
    }
}
