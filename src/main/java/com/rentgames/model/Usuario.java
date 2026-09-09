package com.rentgames.model;

public class Usuario {
    public enum NivelPermissao { ADMIN, FUNCIONARIO }

    private int id;
    private String nomeCompleto;
    private String email;
    private String senhaHash;
    private String contato;
    private String cpf;
    private String endereco;
    private NivelPermissao nivelPermissao;

    public Usuario(int id, String nomeCompleto, String email, String senhaHash,
                    String contato, String cpf, String endereco, NivelPermissao nivelPermissao) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senhaHash = senhaHash;
        this.contato = contato;
        this.cpf = cpf;
        this.endereco = endereco;
        this.nivelPermissao = nivelPermissao;
    }

    public int getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public String getContato() { return contato; }
    public String getCpf() { return cpf; }
    public String getEndereco() { return endereco; }
    public NivelPermissao getNivelPermissao() { return nivelPermissao; }

    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setEmail(String email) { this.email = email; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public void setContato(String contato) { this.contato = contato; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setNivelPermissao(NivelPermissao nivelPermissao) { this.nivelPermissao = nivelPermissao; }

    public boolean isAdmin() {
        return nivelPermissao == NivelPermissao.ADMIN;
    }

    @Override
    public String toString() { return nomeCompleto; }
}
