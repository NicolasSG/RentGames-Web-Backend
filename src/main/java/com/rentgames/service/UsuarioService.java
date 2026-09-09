package com.rentgames.service;

import com.rentgames.model.Usuario;
import com.rentgames.repository.UsuarioRepository;
import com.rentgames.security.PasswordHasher;
import com.rentgames.service.exception.RegraNegocioException;
import com.rentgames.service.exception.ValidacaoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Regras de negocio de Usuario/autenticacao, extraidas de
 * view.UsuariosPanel, view.LoginScreen e view.RegisterScreen.
 *
 * Na versao original, dao.UsuarioDAO.autenticar(email, senha) fazia
 * "SELECT * FROM usuarios WHERE email = ? AND senha = ?" comparando a senha
 * em texto puro. Agora a senha e guardada com hash (PasswordHasher), entao a
 * autenticacao busca o usuario por e-mail e confere o hash aqui.
 * Bean gerenciado pelo Spring (Etapa 9).
 */
@Service
public class UsuarioService {

    private static final String SENHA_PADRAO_FUNCIONARIO = "123456";

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrarFuncionario(String nome, String email, String contato, String cpf,
                                         String endereco, Usuario.NivelPermissao nivel) {
        return cadastrar(nome, email, SENHA_PADRAO_FUNCIONARIO, contato, cpf, endereco, nivel);
    }

    public Usuario autoRegistrar(String email, String senha) {
        return cadastrar("Novo Usuario", email, senha, "", "", "", Usuario.NivelPermissao.FUNCIONARIO);
    }

    private Usuario cadastrar(String nome, String email, String senha, String contato, String cpf,
                               String endereco, Usuario.NivelPermissao nivel) {
        if (isBlank(nome) || isBlank(email) || isBlank(senha)) {
            throw new ValidacaoException("Preencha nome, e-mail e senha.");
        }
        if (usuarioRepository.buscarPorEmail(email.trim()).isPresent()) {
            throw new RegraNegocioException("Ja existe um usuario cadastrado com este e-mail.");
        }
        String senhaHash = PasswordHasher.hash(senha);
        return usuarioRepository.salvar(new Usuario(0, nome.trim(), email.trim(), senhaHash,
                contato == null ? "" : contato.trim(),
                cpf == null ? "" : cpf.trim(),
                endereco == null ? "" : endereco.trim(), nivel));
    }

    /** Usado pelos controllers para conferir o perfil de quem esta chamando (ver AutorizacaoFiltro, Etapa 9). */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    public Usuario autenticar(String email, String senha) {
        if (isBlank(email) || isBlank(senha)) {
            throw new ValidacaoException("Preencha e-mail e senha.");
        }
        Optional<Usuario> usuario = usuarioRepository.buscarPorEmail(email.trim());
        if (usuario.isEmpty() || !PasswordHasher.confere(senha, usuario.get().getSenhaHash())) {
            throw new RegraNegocioException("E-mail ou senha incorretos.");
        }
        return usuario.get();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    public void excluir(int id) {
        usuarioRepository.excluir(id);
    }

    private boolean isBlank(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
