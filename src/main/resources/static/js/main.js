/*
 * main.js
 *
 * Comportamento compartilhado pelas paginas internas (depois do login):
 * exige sessao ativa, preenche o topo com o usuario logado, esconde o
 * menu "Usuarios" para quem nao e ADMIN (mesma regra do MainScreen.java
 * no desktop), toggle do menu no mobile e helper de toast/notificacao.
 */

(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", () => {
    RentGames.Sessao.exigirLogin();
    const usuario = RentGames.Sessao.atual();
    if (!usuario) return;

    const nomeEl = document.querySelector("[data-usuario-nome]");
    const nivelEl = document.querySelector("[data-usuario-nivel]");
    if (nomeEl) nomeEl.textContent = usuario.nome;
    if (nivelEl) nivelEl.textContent = usuario.nivel;

    if (usuario.nivel !== "ADMIN") {
      document.querySelectorAll("[data-somente-admin]").forEach((el) => el.remove());
    }

    const logoutBtn = document.querySelector("[data-logout]");
    if (logoutBtn) {
      logoutBtn.addEventListener("click", () => {
        RentGames.Sessao.sair();
        window.location.href = "login.html";
      });
    }

    const navToggle = document.querySelector("[data-nav-toggle]");
    const shell = document.querySelector(".app-shell");
    if (navToggle && shell) {
      navToggle.addEventListener("click", () => shell.classList.toggle("nav-open"));
      const scrim = shell.querySelector(".nav-scrim");
      if (scrim) scrim.addEventListener("click", () => shell.classList.remove("nav-open"));
    }
  });

  let toastRegion;
  function toastContainer() {
    if (!toastRegion) {
      toastRegion = document.createElement("div");
      toastRegion.className = "toast-region";
      toastRegion.setAttribute("role", "status");
      toastRegion.setAttribute("aria-live", "polite");
      document.body.appendChild(toastRegion);
    }
    return toastRegion;
  }

  function showToast(mensagem, tipo = "success") {
    const el = document.createElement("div");
    el.className = "toast toast-" + tipo;
    el.textContent = mensagem;
    toastContainer().appendChild(el);
    setTimeout(() => el.remove(), 3200);
  }

  window.RentGames = window.RentGames || {};
  window.RentGames.showToast = showToast;
})();
