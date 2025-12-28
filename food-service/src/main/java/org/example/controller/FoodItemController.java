package org.example.controller;

import org.example.model.FoodItem;
import org.example.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = "http://localhost:3000")
public class FoodItemController {

    @Autowired
    private FoodItemRepository repository;

    // Use absolute path to avoid permission issues
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @GetMapping
    public List<FoodItem> getAllFood() {
        return repository.findByStatus("AVAILABLE");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addFood(
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("expiryTime") String expiryTimeStr,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        try {
            FoodItem item = new FoodItem();
            item.setTitle(title);
            item.setCategory(category);
            item.setLocation(location);
            item.setDescription(description);
            item.setStatus("AVAILABLE");

            // SAFE DATE PARSING
            if (expiryTimeStr != null && !expiryTimeStr.isEmpty()) {
                try {
                    // Try standard format (2025-12-27T22:30)
                    item.setExpirationTime(LocalDateTime.parse(expiryTimeStr));
                } catch (Exception e) {
                    System.out.println("Date parse failed for: " + expiryTimeStr);
                    // Fallback: set it to 24 hours from now if parsing fails
                    item.setExpirationTime(LocalDateTime.now().plusHours(24));
                }
            }

            // SAFE FILE UPLOAD
            if (imageFile != null && !imageFile.isEmpty()) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs(); // Force create directory
                }

                String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.copy(imageFile.getInputStream(), filePath);

                // Port 8081 is Food Service
                String fileUrl = "http://localhost:8081/uploads/" + filename;
                item.setImageUrl(fileUrl);
            } else {
                item.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500");
            }

            FoodItem savedItem = repository.save(item);
            return ResponseEntity.ok(savedItem);

        } catch (Exception e) {
            // This prints the REAL error to your IntelliJ console
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }

    }

    // 3. Claim a food item
    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claimFood(@PathVariable Long id) {
        try {
            FoodItem item = repository.findById(id).orElse(null);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }

            // Mark it as claimed
            item.setStatus("CLAIMED");
            repository.save(item);

            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}