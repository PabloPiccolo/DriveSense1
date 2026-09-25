package com.hivesense.hivesense.repository;

import com.hivesense.hivesense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
        Optional<User> findByApiKey(String apiKey);
}