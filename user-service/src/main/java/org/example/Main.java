package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Collections;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);

        // We force the settings here manually to bypass the broken file
        app.setDefaultProperties(Collections.singletonMap("server.port", "8080"));
        System.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/wastefood");
        System.setProperty("spring.datasource.username", "root");
        System.setProperty("spring.datasource.password", ""); // Empty password
        System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");

        app.run(args);
    }
}