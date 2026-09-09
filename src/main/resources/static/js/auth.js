/*
 * auth.js
 *
 * Logica das 3 telas de autenticacao: login.html, cadastro.html e
 * recuperar-senha.html. Cada pagina so chama a funcao correspondente
 * (RentGames.Auth.iniciarLogin(), etc.) no seu proprio <script>.
 *
 * Etapa 9: as chamadas a RentGames.Usuarios.* agora sao assincronas (API
 * REST) - os handlers de submit viraram async/await. Tambem corrigido:
 * o cadastro (auto-registro) usa RentGames.Usuarios.autoRegistrar(...),
 * que bate em POST /api/auth/registrar (rota publica, sempre cria
 * FUNCIONARIO) - nao mais RentGames.Usuarios.cadastrar(...), que agora e
 * POST /api/usuarios, rota exclusiva de ADMIN (ver UsuarioController).
 */

(function () {
  "use strict";

  const { regras, validarForm, validarConfirmacao, ligarValidacaoAoVivo } = RentGames.Validation;

  function ligarTogglesSenha() {
    document.querySelectorAll(".password-toggle").forEach((botao) => {
      botao.addEventListener("click", () => {
        const input = document.getElementById(botao.dataset.target);
        const mostrando = input.type === "text";
        input.type = mostrando ? "password" : "text";
        botao.textContent = mostrando ? "Mostrar" : "Ocultar";
        botao.setAttribute("aria-pressed", String(!mostrando));
      });
    });
  }

  function iniciarLogin() {
    ligarTogglesSenha();
    if (RentGames.Sessao.atual()) {
      window.location.href = "dashboard.html";
      return;
    }
    const form = document.getElementById("form-login");
    const alerta = document.getElementById("login-alerta");
    const schema = { email: [regras.obrigatorio, regras.email], senha: [regras.obrigatorio] };
    ligarValidacaoAoVivo(form, schema);

    form.addEventListener("submit", async (evento) => {
      evento.preventDefault();
      alerta.hidden = true;
      if (!validarForm(form, schema)) return;

      try {
        const usuario = await RentGames.Usuarios.autenticar(form.elements.email.value, form.elements.senha.value);
        RentGames.Sessao.logar(usuario);
        window.location.href = "dashboard.html";
      } catch (erro) {
        alerta.textContent = erro.message;
        alerta.hidden = false;
      }
    });
  }

  function iniciarCadastro() {
    ligarTogglesSenha();
    const form = document.getElementById("form-cadastro");
    const alerta = document.getElementById("cadastro-alerta");
    const schema = {
      email: [regras.obrigatorio, regras.email],
      senha: [regras.obrigatorio, regras.minLength(6)],
    };
    ligarValidacaoAoVivo(form, schema);

    form.elements.confirmarSenha.addEventListener("blur", () => {
      validarConfirmacao(form.elements.senha, form.elements.confirmarSenha);
    });

    form.addEventListener("submit", async (evento) => {
      evento.preventDefault();
      alerta.hidden = true;
      const camposOk = validarForm(form, schema);
      const confirmacaoOk = validarConfirmacao(form.elements.senha, form.elements.confirmarSenha);
      if (!camposOk || !confirmacaoOk) return;

      try {
        await RentGames.Usuarios.autoRegistrar(form.elements.email.value, form.elements.senha.value);
        window.location.href = "login.html?criado=1";
      } catch (erro) {
        alerta.textContent = erro.message;
        alerta.hidden = false;
      }
    });
  }

  function iniciarRecuperarSenha() {
    const form = document.getElementById("form-recuperar");
    const painelSucesso = document.getElementById("recuperar-sucesso");
    const schema = { email: [regras.obrigatorio, regras.email] };
    ligarValidacaoAoVivo(form, schema);

    form.addEventListener("submit", (evento) => {
      evento.preventDefault();
      if (!validarForm(form, schema)) return;
      form.hidden = true;
      painelSucesso.hidden = false;
    });
  }

  function mostrarAvisoContaCriada() {
    const params = new URLSearchParams(window.location.search);
    if (params.get("criado") === "1") {
      const alerta = document.getElementById("login-sucesso");
      if (alerta) alerta.hidden = false;
    }
  }

  window.RentGames = window.RentGames || {};
  window.RentGames.Auth = { iniciarLogin, iniciarCadastro, iniciarRecuperarSenha, mostrarAvisoContaCriada };
})();
