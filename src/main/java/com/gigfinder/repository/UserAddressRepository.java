package com.gigfinder.repository;

import com.gigfinder.model.UserAddress;
import com.gigfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUser(User user);
    Optional<UserAddress> findByUserAndIsDefaultTrue(User user);
    List<UserAddress> findByUserAndAddressType(User user, String addressType);
}
