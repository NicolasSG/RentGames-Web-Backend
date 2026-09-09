package com.rentgames.model;

public class Cliente {
    private int id;
    private String nomeCompleto;
    private String contato;
    private String cpf;
    private String endereco;

    public Cliente(int id, String nomeCompleto, String contato, String cpf, String endereco) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.contato = contato;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    public int getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getContato() { return contato; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }

    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setContato(String contato) { this.contato = contato; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    @Override
    public String toString() { return nomeCompleto; }
}
