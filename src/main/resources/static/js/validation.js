/*
 * validation.js
 *
 * Helpers genericos de validacao de formulario em JavaScript puro
 * (sem bibliotecas externas). Cada pagina define suas regras chamando
 * RentGames.Validation.validarCampo(...) e RentGames.Validation.validarForm(...).
 */

(function (global) {
  "use strict";

  const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  const regras = {
    obrigatorio: (valor) => String(valor || "").trim().length > 0 || "Campo obrigatorio.",
    email: (valor) => REGEX_EMAIL.test(String(valor || "").trim()) || "Informe um e-mail valido.",
    numero: (valor) => (valor === "" || !isNaN(Number(valor))) || "Informe um numero valido.",
    inteiroPositivo: (valor) => (Number.isInteger(Number(valor)) && Number(valor) >= 0) || "Informe um numero inteiro valido.",
    valorPositivo: (valor) => (!isNaN(Number(valor)) && Number(valor) > 0) || "Informe um valor maior que zero.",
    minLength: (min) => (valor) => String(valor || "").length >= min || `Minimo de ${min} caracteres.`,
  };

  function validarCampo(input, validadores) {
    const valor = input.value;
    for (const validar of validadores) {
      const resultado = validar(valor);
      if (resultado !== true) {
        marcarInvalido(input, resultado);
        return false;
      }
    }
    marcarValido(input);
    return true;
  }

  function marcarInvalido(input, mensagem) {
    input.setAttribute("aria-invalid", "true");
    const erro = document.getElementById(input.id + "-erro");
    if (erro) erro.textContent = mensagem;
  }

  function marcarValido(input) {
    input.removeAttribute("aria-invalid");
    const erro = document.getElementById(input.id + "-erro");
    if (erro) erro.textContent = "";
  }

  /**
   * Valida um formulario inteiro.
   * @param {HTMLFormElement} form
   * @param {Object} schema  { nomeDoCampo: [validadores...] }
   * @returns {boolean} true se tudo valido
   */
  function validarForm(form, schema) {
    let valido = true;
    for (const nomeCampo of Object.keys(schema)) {
      const input = form.elements[nomeCampo];
      if (!input) continue;
      const ok = validarCampo(input, schema[nomeCampo]);
      valido = valido && ok;
    }
    return valido;
  }

  function validarConfirmacao(inputSenha, inputConfirmacao) {
    if (inputConfirmacao.value !== inputSenha.value) {
      marcarInvalido(inputConfirmacao, "As senhas nao coincidem.");
      return false;
    }
    marcarValido(inputConfirmacao);
    return true;
  }

  /** Liga validacao "ao vivo" (no blur e no input apos o primeiro erro). */
  function ligarValidacaoAoVivo(form, schema) {
    Object.keys(schema).forEach((nomeCampo) => {
      const input = form.elements[nomeCampo];
      if (!input) return;
      let jaValidouUmaVez = false;
      input.addEventListener("blur", () => {
        jaValidouUmaVez = true;
        validarCampo(input, schema[nomeCampo]);
      });
      input.addEventListener("input", () => {
        if (jaValidouUmaVez) validarCampo(input, schema[nomeCampo]);
      });
    });
  }

  global.RentGames = global.RentGames || {};
  global.RentGames.Validation = {
    regras, validarCampo, validarForm, validarConfirmacao, ligarValidacaoAoVivo,
    marcarInvalido, marcarValido,
  };
})(window);
