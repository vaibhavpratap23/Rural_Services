package com.gigfinder.controller;

import com.gigfinder.dto.ServicePackageDTO;
import com.gigfinder.model.ServicePackage;
import com.gigfinder.service.ServicePackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-packages")
@RequiredArgsConstructor
public class ServicePackageController {
    
    private final ServicePackageService packageService;
    
    @PostMapping
    public ResponseEntity<?> createPackage(@Valid @RequestBody ServicePackageDTO packageDTO) {
        try {
            ServicePackage servicePackage = packageService.createPackage(packageDTO);
            return ResponseEntity.ok(convertToDTO(servicePackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating package: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllPackages() {
        try {
            List<ServicePackage> packages = packageService.getAllActivePackages();
            return ResponseEntity.ok(packages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching packages: " + e.getMessage());
        }
    }
    
    @GetMapping("/{packageId}")
    public ResponseEntity<?> getPackageById(@PathVariable Long packageId) {
        try {
            ServicePackage servicePackage = packageService.getPackageById(packageId);
            return ResponseEntity.ok(convertToDTO(servicePackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching package: " + e.getMessage());
        }
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getPackagesByCategory(@PathVariable Long categoryId) {
        try {
            List<ServicePackage> packages = packageService.getPackagesByCategory(categoryId);
            return ResponseEntity.ok(packages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching packages by category: " + e.getMessage());
        }
    }
    
    @PutMapping("/{packageId}")
    public ResponseEntity<?> updatePackage(@PathVariable Long packageId, @Valid @RequestBody ServicePackageDTO packageDTO) {
        try {
            ServicePackage servicePackage = packageService.updatePackage(packageId, packageDTO);
            return ResponseEntity.ok(convertToDTO(servicePackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating package: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{packageId}")
    public ResponseEntity<?> deletePackage(@PathVariable Long packageId) {
        try {
            packageService.deletePackage(packageId);
            return ResponseEntity.ok("Package deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting package: " + e.getMessage());
        }
    }
    
    @PutMapping("/{packageId}/activate")
    public ResponseEntity<?> activatePackage(@PathVariable Long packageId) {
        try {
            ServicePackage servicePackage = packageService.activatePackage(packageId);
            return ResponseEntity.ok(convertToDTO(servicePackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error activating package: " + e.getMessage());
        }
    }
    
    @PutMapping("/{packageId}/deactivate")
    public ResponseEntity<?> deactivatePackage(@PathVariable Long packageId) {
        try {
            ServicePackage servicePackage = packageService.deactivatePackage(packageId);
            return ResponseEntity.ok(convertToDTO(servicePackage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deactivating package: " + e.getMessage());
        }
    }
    
    private ServicePackageDTO convertToDTO(ServicePackage servicePackage) {
        ServicePackageDTO dto = new ServicePackageDTO();
        dto.setId(servicePackage.getId());
        dto.setName(servicePackage.getName());
        dto.setDescription(servicePackage.getDescription());
        dto.setCategoryId(servicePackage.getCategory() != null ? servicePackage.getCategory().getId().longValue() : null);
        dto.setCategoryName(servicePackage.getCategory() != null ? servicePackage.getCategory().getName() : null);
        dto.setBasePrice(servicePackage.getBasePrice());
        dto.setEstimatedHours(servicePackage.getEstimatedHours());
        dto.setPackageType(servicePackage.getPackageType());
        dto.setIsActive(servicePackage.getIsActive());
        dto.setFeatures(servicePackage.getFeatures());
        dto.setCreatedAt(servicePackage.getCreatedAt());
        return dto;
    }
}
