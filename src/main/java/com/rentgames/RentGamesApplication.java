package com.rentgames;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do back-end web (Etapa 9 do PI). Substitui a classe
 * Main.java (Swing) da versao desktop: aqui o "ponto de entrada" sobe um
 * servidor HTTP embutido (Tomcat) que expoe a API REST em /api/** e serve
 * o front-end estatico da Etapa 8 (src/main/resources/static).
 */
@SpringBootApplication
public class RentGamesApplication {
    public static void main(String[] args) {
        SpringApplication.run(RentGamesApplication.class, args);
    }
}
