package com.gigfinder.controller;

import com.gigfinder.model.Category;
import com.gigfinder.model.SubCategory;
import com.gigfinder.model.ClientProfile;  // Add this
import com.gigfinder.model.User;           // Add this
import com.gigfinder.model.enums.Role;     // Add this
import com.gigfinder.repository.CategoryRepository;
import com.gigfinder.repository.SubCategoryRepository;
import com.gigfinder.repository.ClientProfileRepository;
import com.gigfinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final UserRepository userRepository;
    
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @GetMapping("/categories/{id}/subcategories")
    public List<SubCategory> getSubCategories(@PathVariable Long id) {
        return subCategoryRepository.findByCategoryId(id);
    }
    
    // NEW: Create test client endpoint
    @PostMapping("/test-client")
    public ClientProfile createTestClient() {
        // First create a user
        User testUser = User.builder()
            .name("Test Client")
            .phone("9999999999")
            .email("test@example.com")
            .role(Role.CLIENT)
            .build();
        
        User savedUser = userRepository.save(testUser);
        
        // Then create client profile
        ClientProfile testClient = ClientProfile.builder()
            .user(savedUser)
            .address("123 Test Street, Mumbai")
            .build();
        
        return clientProfileRepository.save(testClient);
    }
}
