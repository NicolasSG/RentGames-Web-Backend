package com.rentgames.model;

import java.time.LocalDate;

public class Aluguel {
    private int id;
    private String nomeCliente;
    private String contato;
    private String cpf;
    private String endereco;
    private Jogo jogo;
    private int diasAluguel;
    private LocalDate dataInicio;
    private LocalDate dataDevolucao;
    private boolean devolvido;

    public Aluguel(int id, String nomeCliente, String contato, String cpf,
                    String endereco, Jogo jogo, int diasAluguel, LocalDate dataInicio) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.contato = contato;
        this.cpf = cpf;
        this.endereco = endereco;
        this.jogo = jogo;
        this.diasAluguel = diasAluguel;
        this.dataInicio = dataInicio;
        this.dataDevolucao = dataInicio.plusDays(diasAluguel);
        this.devolvido = false;
    }

    public int getId() { return id; }
    public String getNomeCliente() { return nomeCliente; }
    public String getContato() { return contato; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }
    public Jogo getJogo() { return jogo; }
    public int getDiasAluguel() { return diasAluguel; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public boolean isDevolvido() { return devolvido; }
    public void setDevolvido(boolean devolvido) { this.devolvido = devolvido; }

    public boolean isAtrasado() {
        return !devolvido && LocalDate.now().isAfter(dataDevolucao);
    }

    public String getStatusDescricao() {
        if (devolvido) return "Devolvido";
        return isAtrasado() ? "Atrasado!" : "Ativo";
    }
}
