/*
 * caixa.js - tela de caixa (RF11, RF12). O saldo mostrado vem de
 * GET /api/transacoes/saldo, que usa TransacaoService.calcularSaldo() no
 * servidor (Etapa 7: metodo puro e testado com JUnit). O "preview" de novo
 * saldo enquanto o usuario digita e so uma estimativa no cliente - quem
 * calcula o saldo de verdade sempre e o servidor.
 * Etapa 9: RentGames.Transacoes.* agora fala com a API REST (assincrono).
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    const { regras, validarForm, ligarValidacaoAoVivo } = RentGames.Validation;
    const form = document.getElementById("form-transacao");
    const corpoTabela = document.getElementById("transacoes-tbody");
    const vazio = document.getElementById("transacoes-vazio");
    const saldoEl = document.getElementById("caixa-saldo");
    const previewEl = document.getElementById("caixa-preview");

    const schema = {
      descricao: [regras.obrigatorio],
      valor: [regras.obrigatorio, regras.valorPositivo],
      data: [regras.obrigatorio],
    };
    ligarValidacaoAoVivo(form, schema);

    async function atualizarSaldo() {
      const saldo = await RentGames.Transacoes.calcularSaldo();
      saldoEl.textContent = RentGames.formatarMoeda(saldo);
      saldoEl.classList.toggle("text-danger", saldo < 0);
    }

    async function render() {
      const transacoes = await RentGames.Transacoes.listar();
      corpoTabela.innerHTML = "";
      vazio.hidden = transacoes.length > 0;

      transacoes.forEach((t) => {
        const tr = document.createElement("tr");
        const classe = t.tipo === "ENTRADA" ? "badge-success" : "badge-danger";
        const sinal = t.tipo === "ENTRADA" ? "+" : "-";
        tr.innerHTML = `
          <td>${t.id}</td>
          <td>${escapeHtml(t.descricao)}</td>
          <td><span class="badge ${classe}">${t.tipo}</span></td>
          <td>${sinal} ${RentGames.formatarMoeda(t.valor)}</td>
          <td>${RentGames.formatarData(t.data)}</td>`;
        corpoTabela.appendChild(tr);
      });
      await atualizarSaldo();
    }

    function escapeHtml(str) {
      const div = document.createElement("div");
      div.textContent = str;
      return div.innerHTML;
    }

    // Preview dinamico: mostra qual seria o novo saldo enquanto o usuario
    // ainda esta digitando, antes mesmo de enviar o formulario.
    async function atualizarPreview() {
      const valor = Number(form.elements.valor.value);
      if (!valor || isNaN(valor)) {
        previewEl.textContent = "";
        return;
      }
      const tipo = form.elements.tipo.value;
      const saldoAtual = await RentGames.Transacoes.calcularSaldo();
      const novoSaldo = tipo === "ENTRADA" ? saldoAtual + valor : saldoAtual - valor;
      previewEl.textContent = `Novo saldo apos esta transacao: ${RentGames.formatarMoeda(novoSaldo)}`;
    }
    form.elements.valor.addEventListener("input", atualizarPreview);
    form.elements.tipo.addEventListener("change", atualizarPreview);

    form.addEventListener("submit", async (evento) => {
      evento.preventDefault();
      if (!validarForm(form, schema)) return;

      try {
        await RentGames.Transacoes.registrarManual(
          form.elements.descricao.value,
          form.elements.valor.value,
          form.elements.data.value,
          form.elements.tipo.value
        );
        form.reset();
        form.elements.data.value = RentGames.hoje();
        previewEl.textContent = "";
        RentGames.showToast("Transacao registrada!");
        render();
      } catch (erro) {
        RentGames.showToast(erro.message, "danger");
      }
    });

    form.elements.data.value = RentGames.hoje();
    render();
  });
})();
