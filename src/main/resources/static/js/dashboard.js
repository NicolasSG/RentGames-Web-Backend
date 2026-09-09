/*
 * dashboard.js - tela inicial: resumo rapido (RF03-RF13 combinados).
 * Etapa 9: chamadas assincronas (API REST); aluguel.jogo.nome e
 * aluguel.statusDescricao vem prontos da API (ver alugueis.js).
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", async () => {
    const [jogos, clientes, alugueis, saldo] = await Promise.all([
      RentGames.Jogos.listar(),
      RentGames.Clientes.listar(),
      RentGames.Alugueis.listar(),
      RentGames.Transacoes.calcularSaldo(),
    ]);

    document.getElementById("stat-jogos").textContent = jogos.length;
    document.getElementById("stat-clientes").textContent = clientes.length;

    const ativos = alugueis.filter((a) => !a.devolvido);
    document.getElementById("stat-alugueis").textContent = ativos.length;

    const saldoEl = document.getElementById("stat-saldo");
    saldoEl.textContent = RentGames.formatarMoeda(saldo);
    saldoEl.classList.toggle("text-danger", saldo < 0);

    const corpoTabela = document.getElementById("dashboard-alugueis-tbody");
    const vazio = document.getElementById("dashboard-alugueis-vazio");
    const recentes = alugueis.slice(0, 5);
    vazio.hidden = recentes.length > 0;

    recentes.forEach((aluguel) => {
      // ver issues#4: usar os booleanos prontos da API, nao comparar o texto
      // de statusDescricao ("Atrasado!", com exclamacao).
      const status = aluguel.statusDescricao;
      const classe = aluguel.devolvido ? "badge-neutral" : aluguel.atrasado ? "badge-danger" : "badge-success";
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${escapeHtml(aluguel.nomeCliente)}</td>
        <td>${escapeHtml(aluguel.jogo.nome)}</td>
        <td>${RentGames.formatarData(aluguel.dataDevolucao)}</td>
        <td><span class="badge ${classe}">${status}</span></td>`;
      corpoTabela.appendChild(tr);
    });

    function escapeHtml(str) {
      const div = document.createElement("div");
      div.textContent = str;
      return div.innerHTML;
    }
  });
})();
