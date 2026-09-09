package com.rentgames.web;

import com.rentgames.model.Usuario;
import com.rentgames.service.UsuarioService;
import com.rentgames.service.exception.RegraNegocioException;
import com.rentgames.web.dto.ErrorResponse;
import com.rentgames.web.dto.LoginRequest;
import com.rentgames.web.dto.RegistroRequest;
import com.rentgames.web.dto.UsuarioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RF01 (login) e auto-registro de usuario (Etapas 6/8). Sem Spring Security
 * nesta etapa: a resposta do login e o "comprovante" de autenticacao que o
 * front-end guarda (ver js/api.js) e reenvia como cabecalho X-User-Email nas
 * chamadas que precisam saber quem esta logado (ex.: /api/usuarios, so ADMIN).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Credenciais invalidas sao 401 (Unauthorized), nao o 409 (Conflict)
        // que GlobalExceptionHandler usa por padrao para RegraNegocioException
        // - "e-mail ou senha incorretos" nao e um conflito de estado como
        // "aluguel ja devolvido". Bug encontrado no teste manual MT-02
        // (Etapa 7) e registrado em issues#1 do repositorio.
        try {
            Usuario usuario = usuarioService.autenticar(request.email(), request.senha());
            return ResponseEntity.ok(UsuarioResponse.de(usuario));
        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody RegistroRequest request) {
        Usuario usuario = usuarioService.autoRegistrar(request.email(), request.senha());
        return UsuarioResponse.de(usuario);
    }
}
