/*
 * clientes.js - tela de clientes (RF04).
 * Etapa 9: RentGames.Clientes.* agora fala com a API REST (assincrono).
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    const { regras, validarForm, ligarValidacaoAoVivo } = RentGames.Validation;
    const form = document.getElementById("form-cliente");
    const corpoTabela = document.getElementById("clientes-tbody");
    const vazio = document.getElementById("clientes-vazio");
    const busca = document.getElementById("clientes-busca");

    const schema = {
      nomeCompleto: [regras.obrigatorio],
      contato: [regras.obrigatorio],
      endereco: [regras.obrigatorio],
    };
    ligarValidacaoAoVivo(form, schema);

    async function render(filtro = "") {
      const termo = filtro.trim().toLowerCase();
      const todos = await RentGames.Clientes.listar();
      const clientes = todos.filter(
        (c) =>
          !termo ||
          c.nomeCompleto.toLowerCase().includes(termo) ||
          c.contato.includes(termo) ||
          (c.cpf || "").includes(termo)
      );

      corpoTabela.innerHTML = "";
      vazio.hidden = clientes.length > 0;

      clientes.forEach((cliente) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${cliente.id}</td>
          <td>${escapeHtml(cliente.nomeCompleto)}</td>
          <td>${escapeHtml(cliente.contato)}</td>
          <td>${escapeHtml(cliente.cpf || "-")}</td>
          <td>${escapeHtml(cliente.endereco)}</td>
          <td class="row-actions">
            <button type="button" class="btn btn-sm btn-danger" data-excluir="${cliente.id}">Excluir</button>
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
        await RentGames.Clientes.cadastrar({
          nomeCompleto: form.elements.nomeCompleto.value,
          contato: form.elements.contato.value,
          cpf: form.elements.cpf.value,
          endereco: form.elements.endereco.value,
        });
        form.reset();
        RentGames.showToast("Cliente cadastrado!");
        render(busca.value);
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    corpoTabela.addEventListener("click", async (evento) => {
      const id = evento.target.dataset.excluir;
      if (!id) return;
      try {
        await RentGames.Clientes.excluir(id);
        RentGames.showToast("Cliente removido.", "danger");
        render(busca.value);
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    busca.addEventListener("input", () => render(busca.value));

    render();
  });
})();
