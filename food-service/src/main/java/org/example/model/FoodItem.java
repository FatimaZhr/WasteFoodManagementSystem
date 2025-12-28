package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "food_items")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // e.g., "Organic Vegetables Box"
    private String description;    // e.g., "Fresh carrots"
    private String location;       // e.g., "Downtown Market"

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    private String status;         // "AVAILABLE" or "CLAIMED"
    private String category;       // "Vegetables", "Bakery"
}