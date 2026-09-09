/*
 * usuarios.js - tela de usuarios, somente ADMIN (RF13).
 * Etapa 9: RentGames.Usuarios.* agora fala com a API REST (assincrono).
 * O esconder-a-tela aqui e so cosmetico (UX) - quem realmente impede um
 * FUNCIONARIO de gerenciar usuarios e o servidor
 * (UsuarioController.exigirAdmin, conferindo o cabecalho X-User-Email).
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    const sessao = RentGames.Sessao.atual();
    if (sessao && sessao.nivel !== "ADMIN") {
      document.getElementById("usuarios-conteudo").hidden = true;
      document.getElementById("usuarios-negado").hidden = false;
      return;
    }

    const { regras, validarForm, ligarValidacaoAoVivo } = RentGames.Validation;
    const form = document.getElementById("form-usuario");
    const corpoTabela = document.getElementById("usuarios-tbody");
    const busca = document.getElementById("usuarios-busca");

    const schema = {
      nomeCompleto: [regras.obrigatorio],
      email: [regras.obrigatorio, regras.email],
    };
    ligarValidacaoAoVivo(form, schema);

    async function render(filtro = "") {
      const termo = filtro.trim().toLowerCase();
      const todos = await RentGames.Usuarios.listar();
      const usuarios = todos.filter(
        (u) => !termo || u.nomeCompleto.toLowerCase().includes(termo) || u.email.toLowerCase().includes(termo)
      );

      corpoTabela.innerHTML = "";
      usuarios.forEach((usuario) => {
        const tr = document.createElement("tr");
        const classe = usuario.nivelPermissao === "ADMIN" ? "badge-warning" : "badge-neutral";
        tr.innerHTML = `
          <td>${usuario.id}</td>
          <td>${escapeHtml(usuario.nomeCompleto)}</td>
          <td>${escapeHtml(usuario.email)}</td>
          <td><span class="badge ${classe}">${usuario.nivelPermissao}</span></td>
          <td class="row-actions">
            <button type="button" class="btn btn-sm btn-danger" data-excluir="${usuario.id}">Excluir</button>
          </td>`;
        corpoTabela.appendChild(tr);
      });
    }

    function escapeHtml(str) {
      const div = document.createElement("div");
      div.textContent = str;
      return div.innerHTML;
    }

    form.addEventListener("submit", async (evento) => {
      evento.preventDefault();
      if (!validarForm(form, schema)) return;

      try {
        await RentGames.Usuarios.cadastrar({
          nomeCompleto: form.elements.nomeCompleto.value,
          email: form.elements.email.value,
          contato: form.elements.contato.value,
          cpf: form.elements.cpf.value,
          endereco: form.elements.endereco.value,
          nivelPermissao: form.elements.nivelPermissao.value,
        });
        form.reset();
        RentGames.showToast("Usuario cadastrado! Senha inicial: 123456");
        render(busca.value);
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    corpoTabela.addEventListener("click", async (evento) => {
      const id = evento.target.dataset.excluir;
      if (!id) return;
      try {
        await RentGames.Usuarios.excluir(id);
        RentGames.showToast("Usuario removido.", "danger");
        render(busca.value);
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    busca.addEventListener("input", () => render(busca.value));

    render();
  });
})();
