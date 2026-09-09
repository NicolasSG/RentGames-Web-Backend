package com.rentgames.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Unica responsabilidade: ler as credenciais de conexao e abrir uma Connection JDBC.
 * Substitui dao.Conexao, que tinha usuario/senha do MySQL fixos no codigo-fonte
 * (e por isso acabavam versionados no Git). Aqui os valores vem de
 * db.properties (fora do controle de versao) ou de variaveis de ambiente,
 * nessa ordem de prioridade: variavel de ambiente > db.properties > valor padrao local.
 */
public final class DatabaseConfig {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            System.err.println("Nao foi possivel ler db.properties: " + e.getMessage());
        }

        URL = env("DB_URL", props.getProperty("db.url", "jdbc:mysql://localhost:3306/rentgames_db"));
        USER = env("DB_USER", props.getProperty("db.user", "root"));
        PASSWORD = env("DB_PASSWORD", props.getProperty("db.password", ""));
    }

    private DatabaseConfig() { }

    private static String env(String varName, String fallback) {
        String value = System.getenv(varName);
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
