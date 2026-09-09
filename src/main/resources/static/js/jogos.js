/*
 * jogos.js - tela de catalogo de jogos (RF03, RF05, RF14).
 * Etapa 9: RentGames.Jogos.* agora fala com a API REST (assincrono).
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    const { regras, validarForm, ligarValidacaoAoVivo } = RentGames.Validation;
    const form = document.getElementById("form-jogo");
    const corpoTabela = document.getElementById("jogos-tbody");
    const vazio = document.getElementById("jogos-vazio");
    const busca = document.getElementById("jogos-busca");

    const schema = {
      nome: [regras.obrigatorio],
      ano: [regras.obrigatorio, regras.inteiroPositivo],
      plataforma: [regras.obrigatorio],
      valorAluguel: [regras.obrigatorio, regras.valorPositivo],
      quantidadeEstoque: [regras.obrigatorio, regras.inteiroPositivo],
      diasAluguelPadrao: [regras.inteiroPositivo],
    };
    ligarValidacaoAoVivo(form, schema);

    async function render(filtro = "") {
      const termo = filtro.trim().toLowerCase();
      const todos = await RentGames.Jogos.listar();
      const jogos = todos.filter(
        (j) => !termo || j.nome.toLowerCase().includes(termo) || j.plataforma.toLowerCase().includes(termo)
      );

      corpoTabela.innerHTML = "";
      vazio.hidden = jogos.length > 0;

      jogos.forEach((jogo) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${jogo.id}</td>
          <td>${escapeHtml(jogo.nome)}</td>
          <td>${escapeHtml(jogo.plataforma)}</td>
          <td>${jogo.ano}</td>
          <td>${RentGames.formatarMoeda(jogo.valorAluguel)}</td>
          <td>${estoqueBadge(jogo.quantidadeEstoque)}</td>
          <td>${jogo.diasAluguelPadrao}</td>
          <td class="row-actions">
            <button type="button" class="btn btn-sm btn-danger" data-excluir="${jogo.id}">Excluir</button>
          </td>`;
        corpoTabela.appendChild(tr);
      });
    }

    function estoqueBadge(quantidade) {
      if (quantidade <= 0) return `<span class="badge badge-danger">Sem estoque</span>`;
      if (quantidade <= 2) return `<span class="badge badge-warning">${quantidade} em estoque</span>`;
      return `<span class="badge badge-success">${quantidade} em estoque</span>`;
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
        await RentGames.Jogos.cadastrar({
          nome: form.elements.nome.value,
          ano: form.elements.ano.value,
          plataforma: form.elements.plataforma.value,
          valorAluguel: form.elements.valorAluguel.value,
          quantidadeEstoque: form.elements.quantidadeEstoque.value,
          diasAluguelPadrao: form.elements.diasAluguelPadrao.value || 7,
        });
        form.reset();
        RentGames.showToast("Jogo cadastrado!");
        render(busca.value);
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    corpoTabela.addEventListener("click", async (evento) => {
      const id = evento.target.dataset.excluir;
      if (!id) return;
      try {
        await RentGames.Jogos.excluir(id);
        RentGames.showToast("Jogo removido.", "danger");
        render(busca.value);
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    busca.addEventListener("input", () => render(busca.value));

    render();
  });
})();
