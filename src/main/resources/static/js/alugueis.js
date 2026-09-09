/*
 * alugueis.js - tela de alugueis (RF05, RF06, RF07, RF08, RF09, RF10).
 * Etapa 9: RentGames.Alugueis.* agora fala com a API REST (assincrono).
 *
 * O JSON de Aluguel que a API devolve tem um objeto "jogo" aninhado (nao um
 * campo achatado "nomeJogo" como no mock em localStorage da Etapa 8) e ja
 * vem com "statusDescricao"/"atrasado" calculados no servidor
 * (com.rentgames.model.Aluguel.getStatusDescricao()/isAtrasado(), que o
 * Jackson serializa automaticamente) - usamos esses campos prontos em vez
 * de recalcular a data no cliente.
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    const { regras, validarForm, ligarValidacaoAoVivo } = RentGames.Validation;
    const form = document.getElementById("form-aluguel");
    const selectJogo = document.getElementById("aluguel-jogo");
    const corpoTabela = document.getElementById("alugueis-tbody");
    const vazio = document.getElementById("alugueis-vazio");

    const schema = {
      nomeCliente: [regras.obrigatorio],
      contato: [regras.obrigatorio],
      endereco: [regras.obrigatorio],
      idJogo: [regras.obrigatorio],
    };
    ligarValidacaoAoVivo(form, schema);

    async function popularCombo() {
      const selecaoAtual = selectJogo.value;
      const disponiveis = await RentGames.Jogos.listarDisponiveis();
      selectJogo.innerHTML = '<option value="">Selecione...</option>' +
        disponiveis
          .map((j) => `<option value="${j.id}">${escapeHtml(j.nome)} - ${RentGames.formatarMoeda(j.valorAluguel)} (${j.quantidadeEstoque} disp.)</option>`)
          .join("");
      if (disponiveis.some((j) => String(j.id) === selecaoAtual)) {
        selectJogo.value = selecaoAtual;
      }
    }

    function statusBadge(aluguel) {
      // aluguel.atrasado vem pronto do servidor (Aluguel.isAtrasado(), Etapa 6/9) -
      // mais confiavel que comparar o texto de statusDescricao (bug corrigido em issues#4:
      // o texto e "Atrasado!", com exclamacao, e a comparacao antiga usava "Atrasado").
      const status = aluguel.statusDescricao;
      const classe = aluguel.devolvido ? "badge-neutral" : aluguel.atrasado ? "badge-danger" : "badge-success";
      return `<span class="badge ${classe}">${status}</span>`;
    }

    async function render() {
      const alugueis = await RentGames.Alugueis.listar();
      corpoTabela.innerHTML = "";
      vazio.hidden = alugueis.length > 0;

      alugueis.forEach((aluguel) => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${aluguel.id}</td>
          <td>${escapeHtml(aluguel.nomeCliente)}</td>
          <td>${escapeHtml(aluguel.jogo.nome)}</td>
          <td>${RentGames.formatarData(aluguel.dataInicio)}</td>
          <td>${RentGames.formatarData(aluguel.dataDevolucao)}</td>
          <td>${statusBadge(aluguel)}</td>
          <td class="row-actions">
            ${aluguel.devolvido ? "" : `<button type="button" class="btn btn-sm" data-devolver="${aluguel.id}">Marcar devolvido</button>`}
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
        await RentGames.Alugueis.registrar({
          nomeCliente: form.elements.nomeCliente.value,
          contato: form.elements.contato.value,
          cpf: form.elements.cpf.value,
          endereco: form.elements.endereco.value,
          idJogo: form.elements.idJogo.value,
          dias: form.elements.dias.value,
        });
        form.reset();
        RentGames.showToast("Aluguel cadastrado!");
        await popularCombo();
        await render();
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    corpoTabela.addEventListener("click", async (evento) => {
      const id = evento.target.dataset.devolver;
      if (!id) return;
      try {
        await RentGames.Alugueis.registrarDevolucao(id);
        RentGames.showToast("Devolucao registrada!");
        await popularCombo();
        await render();
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    popularCombo();
    render();
  });
})();
