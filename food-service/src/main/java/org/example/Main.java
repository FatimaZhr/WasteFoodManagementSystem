package org.example;

import org.example.rmi.FoodServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // This runs automatically when the server starts
    @Bean
    CommandLineRunner startRmiServer(FoodServiceImpl foodService) {
        return args -> {
            try {
                System.out.println("⏳ Starting RMI Registry...");

                // Create the registry on port 1099
                Registry registry = LocateRegistry.createRegistry(1099);

                // Bind our service so the User Service can find it
                registry.rebind("FoodService", foodService);

                System.out.println("✅ RMI Server is running on port 1099");
            } catch (Exception e) {
                System.err.println("❌ RMI Server failed to start:");
                e.printStackTrace();
            }
        };
    }
}