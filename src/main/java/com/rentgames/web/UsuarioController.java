package com.rentgames.web;

import com.rentgames.model.Usuario;
import com.rentgames.service.UsuarioService;
import com.rentgames.service.exception.AutorizacaoException;
import com.rentgames.web.dto.UsuarioRequest;
import com.rentgames.web.dto.UsuarioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RF13 - somente ADMIN. Na Etapa 8, o front-end so *escondia* o menu
 * "Usuarios" para quem nao era ADMIN - qualquer um podia chamar a API
 * direto (nao existia API ainda, mas a checagem em si era so estetica).
 * Aqui a Etapa 9 corrige isso de verdade: cada chamada tem que informar
 * quem esta fazendo a chamada (cabecalho X-User-Email, preenchido pelo
 * front-end com o e-mail salvo na sessao apos o login) e o servidor
 * confere se esse usuario existe e e ADMIN antes de deixar passar.
 *
 * Isso ainda nao e um mecanismo de autenticacao "de verdade" (nao ha
 * token nem senha reenviada) - so uma autorizacao minima para fechar a
 * lacuna mais obvia. Um back-end com Spring Security e JWT/sessao ficaria
 * para uma proxima iteracao do projeto.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final String CABECALHO_USUARIO = "X-User-Email";

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos(@RequestHeader(CABECALHO_USUARIO) String emailSolicitante) {
        exigirAdmin(emailSolicitante);
        return usuarioService.listarTodos().stream().map(UsuarioResponse::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestHeader(CABECALHO_USUARIO) String emailSolicitante,
                                      @RequestBody UsuarioRequest request) {
        exigirAdmin(emailSolicitante);
        Usuario.NivelPermissao nivel = parseNivel(request.nivelPermissao());
        Usuario usuario = usuarioService.cadastrarFuncionario(request.nomeCompleto(), request.email(),
                request.contato(), request.cpf(), request.endereco(), nivel);
        return UsuarioResponse.de(usuario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@RequestHeader(CABECALHO_USUARIO) String emailSolicitante, @PathVariable int id) {
        exigirAdmin(emailSolicitante);
        usuarioService.excluir(id);
    }

    private void exigirAdmin(String emailSolicitante) {
        Usuario solicitante = usuarioService.buscarPorEmail(emailSolicitante)
                .orElseThrow(() -> new AutorizacaoException("Usuario nao autenticado."));
        if (!solicitante.isAdmin()) {
            throw new AutorizacaoException("Apenas usuarios ADMIN podem gerenciar usuarios.");
        }
    }

    private Usuario.NivelPermissao parseNivel(String texto) {
        try {
            return Usuario.NivelPermissao.valueOf(String.valueOf(texto).trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Usuario.NivelPermissao.FUNCIONARIO;
        }
    }
}
