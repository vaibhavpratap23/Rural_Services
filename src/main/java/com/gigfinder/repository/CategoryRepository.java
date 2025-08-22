package com.gigfinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gigfinder.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
