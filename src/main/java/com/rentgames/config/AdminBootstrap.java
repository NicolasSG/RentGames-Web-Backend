package com.rentgames.config;

import com.rentgames.model.Usuario;
import com.rentgames.service.UsuarioService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Resolve issues#2: sem isto, nao havia nenhuma rota publica capaz de criar
 * o primeiro usuario ADMIN (POST /api/auth/registrar sempre cria
 * FUNCIONARIO; POST /api/usuarios exige um ADMIN ja existente para
 * autorizar). Na subida da aplicacao, se ainda nao existir nenhum ADMIN no
 * banco, cria um com credenciais padrao e avisa no log para troca-las.
 */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final String EMAIL_PADRAO = "admin@rentgames.com";
    // Mesma senha inicial padrao ja usada em UsuarioService para novos
    // funcionarios cadastrados por um ADMIN (cadastrarFuncionario) - aqui
    // reaproveitamos o mesmo metodo, so trocando o nivel para ADMIN.
    private static final String SENHA_PADRAO = "123456";

    private final UsuarioService usuarioService;

    public AdminBootstrap(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean existeAdmin = usuarioService.listarTodos().stream().anyMatch(Usuario::isAdmin);
        if (existeAdmin) {
            return;
        }
        usuarioService.cadastrarFuncionario("Administrador", EMAIL_PADRAO, "", "", "",
                Usuario.NivelPermissao.ADMIN);

        System.out.println("========================================================");
        System.out.println("Nenhum usuario ADMIN encontrado - criado automaticamente:");
        System.out.println("  e-mail: " + EMAIL_PADRAO);
        System.out.println("  senha : " + SENHA_PADRAO + "  (troque no primeiro acesso)");
        System.out.println("========================================================");
    }
}
