/*
 * store.js
 *
 * Etapa 8: isto era um mock de front-end (dados no localStorage do
 * navegador, sem back-end). Etapa 9: virou o cliente da API REST real
 * (com.rentgames.web.*), que fala com MySQL via o repository/service
 * reaproveitados da Etapa 6. A troca foi so "de onde vem o dado" - os
 * nomes do objeto RentGames.* continuam os mesmos (Jogos, Clientes,
 * Alugueis, Transacoes, Usuarios, Sessao) para nao precisar redesenhar as
 * telas, exatamente como planejado em docs/FRONTEND.md da Etapa 8.
 *
 * Diferenca importante: toda chamada agora e assincrona (fetch = Promise),
 * entao os arquivos js/*.js de cada pagina passaram a usar async/await.
 *
 * IMPORTANTE sobre autenticacao: esta etapa nao usa Spring Security. O
 * "comprovante" de login e a resposta de /api/auth/login, guardada em
 * sessionStorage; para as rotas que exigem ADMIN (/api/usuarios), o
 * e-mail da sessao e reenviado no cabecalho X-User-Email e conferido no
 * servidor (ver com.rentgames.web.UsuarioController.exigirAdmin).
 */

(function (global) {
  "use strict";

  const API = "/api";
  const CHAVE_SESSAO = "rentgames.sessao";

  async function requisitar(caminho, opcoes = {}) {
    const headers = { "Content-Type": "application/json", ...(opcoes.headers || {}) };
    const sessao = Sessao.atual();
    if (sessao) {
      headers["X-User-Email"] = sessao.email;
    }
    const resposta = await fetch(API + caminho, { ...opcoes, headers });
    if (resposta.status === 204) {
      return null;
    }
    const corpo = await resposta.json().catch(() => null);
    if (!resposta.ok) {
      const mensagem = (corpo && corpo.error) || `Erro inesperado (HTTP ${resposta.status}).`;
      throw new Error(mensagem);
    }
    return corpo;
  }

  function hoje() {
    return new Date().toISOString().slice(0, 10);
  }

  function formatarData(isoDate) {
    if (!isoDate) return "-";
    const [ano, mes, dia] = isoDate.split("-");
    return `${dia}/${mes}/${ano}`;
  }

  function formatarMoeda(valor) {
    return "R$ " + Number(valor).toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  // Regras replicadas do backend (com.rentgames.model/service) so para
  // decisao de exibicao no cliente (o servidor e quem realmente aplica
  // as regras - ver AluguelService/JogoService/TransacaoService).
  function jogoTemEstoque(jogo) {
    return jogo.quantidadeEstoque > 0;
  }

  function aluguelEstaAtrasado(aluguel) {
    if (aluguel.devolvido) return false;
    return aluguel.dataDevolucao < hoje();
  }

  function aluguelStatus(aluguel) {
    if (aluguel.devolvido) return "Devolvido";
    return aluguelEstaAtrasado(aluguel) ? "Atrasado" : "Ativo";
  }

  const Jogos = {
    listar() { return requisitar("/jogos"); },
    listarDisponiveis() { return requisitar("/jogos/disponiveis"); },
    cadastrar(dados) {
      return requisitar("/jogos", { method: "POST", body: JSON.stringify(dados) });
    },
    excluir(id) {
      return requisitar(`/jogos/${id}`, { method: "DELETE" });
    },
  };

  const Clientes = {
    listar() { return requisitar("/clientes"); },
    cadastrar(dados) {
      return requisitar("/clientes", { method: "POST", body: JSON.stringify(dados) });
    },
    excluir(id) {
      return requisitar(`/clientes/${id}`, { method: "DELETE" });
    },
  };

  const Alugueis = {
    listar() { return requisitar("/alugueis"); },
    registrar(dados) {
      return requisitar("/alugueis", { method: "POST", body: JSON.stringify(dados) });
    },
    registrarDevolucao(id) {
      return requisitar(`/alugueis/${id}/devolucao`, { method: "POST" });
    },
  };

  const Transacoes = {
    listar() { return requisitar("/transacoes"); },
    async calcularSaldo() {
      const resposta = await requisitar("/transacoes/saldo");
      return resposta.saldo;
    },
    registrarManual(descricao, valor, data, tipo) {
      return requisitar("/transacoes", {
        method: "POST",
        body: JSON.stringify({ descricao, valor, data, tipo }),
      });
    },
  };

  const Usuarios = {
    listar() { return requisitar("/usuarios"); },
    cadastrar(dados) {
      return requisitar("/usuarios", { method: "POST", body: JSON.stringify(dados) });
    },
    excluir(id) {
      return requisitar(`/usuarios/${id}`, { method: "DELETE" });
    },
    autenticar(email, senha) {
      return requisitar("/auth/login", { method: "POST", body: JSON.stringify({ email, senha }) });
    },
    autoRegistrar(email, senha) {
      return requisitar("/auth/registrar", { method: "POST", body: JSON.stringify({ email, senha }) });
    },
  };

  const Sessao = {
    logar(usuarioResponse) {
      sessionStorage.setItem(CHAVE_SESSAO, JSON.stringify({
        id: usuarioResponse.id,
        nome: usuarioResponse.nomeCompleto,
        email: usuarioResponse.email,
        nivel: usuarioResponse.nivelPermissao,
      }));
    },
    atual() {
      try {
        const raw = sessionStorage.getItem(CHAVE_SESSAO);
        return raw ? JSON.parse(raw) : null;
      } catch (e) {
        return null;
      }
    },
    sair() {
      sessionStorage.removeItem(CHAVE_SESSAO);
    },
    exigirLogin() {
      if (!this.atual()) {
        window.location.href = "login.html";
      }
    },
  };

  global.RentGames = {
    Jogos, Clientes, Alugueis, Transacoes, Usuarios, Sessao,
    formatarData, formatarMoeda, hoje,
    jogoTemEstoque, aluguelEstaAtrasado, aluguelStatus,
  };
})(window);
