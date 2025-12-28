package org.example.repository;

import org.example.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    // Find all food that hasn't been claimed yet
    List<FoodItem> findByStatus(String status);
}