package com.rentgames.model;

public class Jogo {
    private int id;
    private String nome;
    private int ano;
    private String plataforma;
    private double valorAluguel;
    private int diasAluguelPadrao;
    private int quantidadeEstoque;

    public Jogo(int id, String nome, int ano, String plataforma,
                double valorAluguel, int diasAluguelPadrao, int quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.ano = ano;
        this.plataforma = plataforma;
        this.valorAluguel = valorAluguel;
        this.diasAluguelPadrao = diasAluguelPadrao;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getAno() { return ano; }
    public String getPlataforma() { return plataforma; }
    public double getValorAluguel() { return valorAluguel; }
    public int getDiasAluguelPadrao() { return diasAluguelPadrao; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }

    public void setNome(String nome) { this.nome = nome; }
    public void setAno(int ano) { this.ano = ano; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public void setValorAluguel(double valorAluguel) { this.valorAluguel = valorAluguel; }
    public void setDiasAluguelPadrao(int diasAluguelPadrao) { this.diasAluguelPadrao = diasAluguelPadrao; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public boolean temEstoqueDisponivel() {
        return quantidadeEstoque > 0;
    }

    @Override
    public String toString() { return nome; }
}
