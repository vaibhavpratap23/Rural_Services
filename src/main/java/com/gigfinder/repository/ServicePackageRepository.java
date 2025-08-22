package com.gigfinder.repository;

import com.gigfinder.model.ServicePackage;
import com.gigfinder.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    List<ServicePackage> findByCategory(Category category);
    List<ServicePackage> findByIsActiveTrue();
    List<ServicePackage> findByCategoryAndIsActiveTrue(Category category);
    List<ServicePackage> findByPackageType(String packageType);
}
