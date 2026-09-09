package com.rentgames.service;

import com.rentgames.repository.AluguelRepository;
import com.rentgames.repository.ClienteRepository;
import com.rentgames.repository.JogoRepository;
import com.rentgames.repository.TransacaoRepository;
import com.rentgames.repository.UsuarioRepository;
import com.rentgames.repository.jdbc.AluguelRepositoryJdbc;
import com.rentgames.repository.jdbc.ClienteRepositoryJdbc;
import com.rentgames.repository.jdbc.JogoRepositoryJdbc;
import com.rentgames.repository.jdbc.TransacaoRepositoryJdbc;
import com.rentgames.repository.jdbc.UsuarioRepositoryJdbc;

/**
 * Composition root: unico ponto do sistema que sabe montar repositories (JDBC)
 * e injeta-los nos services. As telas Swing (view.*) so enxergam os services -
 * nunca instanciam um XxxRepositoryJdbc diretamente, ao contrario da versao
 * original, onde cada JPanel criava seus proprios DAOs com "new".
 *
 * E um Singleton porque a aplicacao desktop so precisa de uma unica instancia
 * de cada service durante toda a execucao.
 */
public final class AppContext {

    private static final AppContext INSTANCE = new AppContext(
            new JogoRepositoryJdbc(),
            new ClienteRepositoryJdbc(),
            new AluguelRepositoryJdbc(),
            new TransacaoRepositoryJdbc(),
            new UsuarioRepositoryJdbc()
    );

    private final JogoService jogoService;
    private final ClienteService clienteService;
    private final AluguelService aluguelService;
    private final TransacaoService transacaoService;
    private final UsuarioService usuarioService;

    public AppContext(JogoRepository jogoRepository, ClienteRepository clienteRepository,
                       AluguelRepository aluguelRepository, TransacaoRepository transacaoRepository,
                       UsuarioRepository usuarioRepository) {
        this.jogoService = new JogoService(jogoRepository);
        this.clienteService = new ClienteService(clienteRepository);
        this.transacaoService = new TransacaoService(transacaoRepository);
        this.aluguelService = new AluguelService(aluguelRepository, this.transacaoService);
        this.usuarioService = new UsuarioService(usuarioRepository);
    }

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public JogoService jogoService() { return jogoService; }
    public ClienteService clienteService() { return clienteService; }
    public AluguelService aluguelService() { return aluguelService; }
    public TransacaoService transacaoService() { return transacaoService; }
    public UsuarioService usuarioService() { return usuarioService; }
}
