package com.gigfinder.service;

import com.gigfinder.dto.ServicePackageDTO;
import com.gigfinder.model.Category;
import com.gigfinder.model.ServicePackage;
import com.gigfinder.repository.CategoryRepository;
import com.gigfinder.repository.ServicePackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePackageService {
    
    private final ServicePackageRepository packageRepository;
    private final CategoryRepository categoryRepository;
    
    public ServicePackage createPackage(ServicePackageDTO packageDTO) {
        Category category = null;
        if (packageDTO.getCategoryId() != null) {
            category = categoryRepository.findById(packageDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }
        
        ServicePackage servicePackage = ServicePackage.builder()
                .name(packageDTO.getName())
                .description(packageDTO.getDescription())
                .category(category)
                .basePrice(packageDTO.getBasePrice())
                .estimatedHours(packageDTO.getEstimatedHours())
                .packageType(packageDTO.getPackageType())
                .isActive(packageDTO.getIsActive() != null ? packageDTO.getIsActive() : true)
                .features(packageDTO.getFeatures())
                .build();
        
        return packageRepository.save(servicePackage);
    }
    
    public List<ServicePackage> getAllActivePackages() {
        return packageRepository.findByIsActiveTrue();
    }
    
    public ServicePackage getPackageById(Long packageId) {
        return packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Service package not found"));
    }
    
    public List<ServicePackage> getPackagesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return packageRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    public ServicePackage updatePackage(Long packageId, ServicePackageDTO packageDTO) {
        ServicePackage servicePackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Service package not found"));
        
        Category category = null;
        if (packageDTO.getCategoryId() != null) {
            category = categoryRepository.findById(packageDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }
        
        servicePackage.setName(packageDTO.getName());
        servicePackage.setDescription(packageDTO.getDescription());
        servicePackage.setCategory(category);
        servicePackage.setBasePrice(packageDTO.getBasePrice());
        servicePackage.setEstimatedHours(packageDTO.getEstimatedHours());
        servicePackage.setPackageType(packageDTO.getPackageType());
        servicePackage.setFeatures(packageDTO.getFeatures());
        
        return packageRepository.save(servicePackage);
    }
    
    public void deletePackage(Long packageId) {
        ServicePackage servicePackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Service package not found"));
        packageRepository.delete(servicePackage);
    }
    
    public ServicePackage activatePackage(Long packageId) {
        ServicePackage servicePackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Service package not found"));
        servicePackage.setIsActive(true);
        return packageRepository.save(servicePackage);
    }
    
    public ServicePackage deactivatePackage(Long packageId) {
        ServicePackage servicePackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Service package not found"));
        servicePackage.setIsActive(false);
        return packageRepository.save(servicePackage);
    }
}
