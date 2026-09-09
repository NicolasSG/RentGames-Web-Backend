package com.rentgames.service;

import com.rentgames.model.Aluguel;
import com.rentgames.model.Jogo;
import com.rentgames.repository.AluguelRepository;
import com.rentgames.service.exception.RegraNegocioException;
import com.rentgames.service.exception.ValidacaoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Orquestra o fluxo de aluguel: valida os dados, confere estoque, grava o
 * aluguel e lanca a transacao financeira de entrada correspondente.
 *
 * Na versao desktop original essa orquestracao estava espalhada em
 * view.AlugueisPanel.btnCadastrarActionPerformed: validacao de campos,
 * checagem de estoque, chamada ao DAO, atualizacao manual do objeto Jogo em
 * memoria e criacao da Transacao apareciam todas juntas no mesmo metodo de
 * evento do Swing. Aqui viram uma unica responsabilidade coesa, que pode ser
 * reutilizada por qualquer interface (Swing, web, testes) e testada sem UI.
 * Bean gerenciado pelo Spring (Etapa 9).
 */
@Service
public class AluguelService {

    private static final int DIAS_ALUGUEL_PADRAO = 7;

    private final AluguelRepository aluguelRepository;
    private final TransacaoService transacaoService;

    public AluguelService(AluguelRepository aluguelRepository, TransacaoService transacaoService) {
        this.aluguelRepository = aluguelRepository;
        this.transacaoService = transacaoService;
    }

    public Aluguel registrar(String nomeCliente, String contato, String cpf, String endereco,
                              Jogo jogo, String diasTexto) {
        if (isBlank(nomeCliente) || isBlank(contato) || isBlank(endereco) || jogo == null) {
            throw new ValidacaoException("Preencha os campos obrigatorios (*).");
        }
        int dias = isBlank(diasTexto) ? DIAS_ALUGUEL_PADRAO : parseDias(diasTexto);
        if (!jogo.temEstoqueDisponivel()) {
            throw new RegraNegocioException("Jogo sem estoque.");
        }

        Aluguel novoAluguel = new Aluguel(0, nomeCliente.trim(), contato.trim(),
                cpf == null ? "" : cpf.trim(), endereco.trim(), jogo, dias, LocalDate.now());

        Aluguel aluguelPersistido = aluguelRepository.salvar(novoAluguel);
        transacaoService.registrarEntradaDeAluguel("Aluguel - " + jogo.getNome(), jogo.getValorAluguel());
        return aluguelPersistido;
    }

    public void registrarDevolucao(Aluguel aluguel) {
        if (aluguel.isDevolvido()) {
            throw new RegraNegocioException("Este aluguel ja foi devolvido.");
        }
        aluguelRepository.registrarDevolucao(aluguel.getId(), aluguel.getJogo().getId());
    }

    public List<Aluguel> listarTodos() {
        return aluguelRepository.listarTodos();
    }

    public Optional<Aluguel> buscarPorId(int id) {
        return aluguelRepository.buscarPorId(id);
    }

    private boolean isBlank(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private int parseDias(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacaoException("Numero de dias invalido.");
        }
    }
}
