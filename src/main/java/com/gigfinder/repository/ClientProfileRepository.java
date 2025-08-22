package com.gigfinder.repository;

import com.gigfinder.model.ClientProfile;
import com.gigfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {

    Optional<ClientProfile> findByUser(User user);  //  <— ADD THIS LINE
}
