package org.example.controller;

import org.example.model.FoodItem;
import org.example.rmi.FoodServiceRMI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bridge/food") // React calls this URL
@CrossOrigin(origins = "http://localhost:3000")
public class BridgeController {

    // Helper to connect to the RMI Server
    private FoodServiceRMI getRmiService() throws Exception {
        // Looks for "FoodService" running on port 1099
        return (FoodServiceRMI) Naming.lookup("rmi://localhost:1099/FoodService");
    }

    // SHARED FOLDER for images (User Service saves them here)
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // 1. GET ALL FOOD (Via RMI)
    @GetMapping
    public List<FoodItem> getAllFood() {
        try {
            System.out.println("BRIDGE: Calling RMI for getAllFood()...");
            return getRmiService().getAllFood();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. CLAIM FOOD (Via RMI)
    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claimFood(@PathVariable Long id) {
        try {
            System.out.println("BRIDGE: Calling RMI for claimFood(" + id + ")...");
            FoodItem updatedItem = getRmiService().claimFood(id);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("RMI Error: " + e.getMessage());
        }
    }

    // 3. ADD FOOD (Uploads locally, sends data via RMI)
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
            // A. Create the object
            FoodItem item = new FoodItem();
            item.setTitle(title);
            item.setCategory(category);
            item.setLocation(location);
            item.setDescription(description);

            // Parse Date
            if (expiryTimeStr != null && !expiryTimeStr.isEmpty()) {
                item.setExpirationTime(LocalDateTime.parse(expiryTimeStr));
            }

            // B. Handle File Upload (Save to disk locally)
            if (imageFile != null && !imageFile.isEmpty()) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.copy(imageFile.getInputStream(), filePath);

                // Note: We point the URL to port 8080 now (User Service serves the images)
                String fileUrl = "http://localhost:8080/uploads/" + filename;
                item.setImageUrl(fileUrl);
            } else {
                item.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500");
            }

            // C. Send the object via RMI to be saved in DB
            System.out.println("BRIDGE: Sending new food via RMI...");
            FoodItem savedItem = getRmiService().addFood(item);

            return ResponseEntity.ok(savedItem);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Bridge Error: " + e.getMessage());
        }
    }
}