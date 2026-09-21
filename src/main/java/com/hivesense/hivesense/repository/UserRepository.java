package com.hivesense.hivesense.repository;

import com.hivesense.hivesense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    
}