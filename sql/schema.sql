-- Schema do RentGames (versao refatorada).
-- Baseado em "DATABASE SQL QUERIES.sql" do projeto desktop original,
-- com um ajuste: a coluna "senha" agora guarda um hash (salt + SHA-256),
-- nao mais a senha em texto puro, entao precisou de mais espaco (VARCHAR 120).

CREATE DATABASE IF NOT EXISTS rentgames_db;
USE rentgames_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(120) NOT NULL,
    contato VARCHAR(20),
    cpf VARCHAR(20),
    endereco VARCHAR(200),
    perfil ENUM('ADMIN', 'FUNCIONARIO') DEFAULT 'FUNCIONARIO'
);

CREATE TABLE IF NOT EXISTS jogos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ano INT,
    plataforma VARCHAR(50),
    valor_aluguel DECIMAL(10, 2) NOT NULL,
    estoque INT DEFAULT 0,
    dias_aluguel INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    cpf VARCHAR(20) UNIQUE,
    endereco VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS alugueis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_cliente VARCHAR(100),
    contato VARCHAR(50),
    cpf VARCHAR(20),
    endereco VARCHAR(200),
    id_jogo INT NOT NULL,
    dias_aluguel INT NOT NULL,
    data_inicio DATE NOT NULL,
    devolvido BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_jogo_aluguel FOREIGN KEY (id_jogo) REFERENCES jogos(id)
);

CREATE TABLE IF NOT EXISTS transacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data VARCHAR(20) NOT NULL
);

-- ---------------------------------------------------------
-- DADOS INICIAIS PARA TESTE (opcional)
-- ---------------------------------------------------------
-- Nao ha mais um INSERT direto de usuario admin aqui: como a senha precisa
-- ser gravada com hash (com.rentgames.security.PasswordHasher), o usuario
-- inicial deve ser criado pela propria aplicacao (tela "Criar conta" ou
-- painel de Usuarios), e nao por um INSERT com senha em texto puro.

INSERT IGNORE INTO jogos (nome, ano, plataforma, valor_aluguel, estoque, dias_aluguel) VALUES
('The Witcher 3', 2015, 'PC', 15.00, 5, 7),
('God of War Ragnarok', 2022, 'PS5', 25.00, 3, 7),
('Elden Ring', 2022, 'PC', 20.00, 4, 7);

INSERT IGNORE INTO transacoes (descricao, tipo, valor, data)
VALUES ('Abertura de Caixa', 'ENTRADA', 200.00, '01/01/2026');

INSERT IGNORE INTO clientes (nome, email, telefone, cpf, endereco)
VALUES ('Ana Clara', 'aninha@email.com', '11999990000', '12345678900', 'Rua Dois');
