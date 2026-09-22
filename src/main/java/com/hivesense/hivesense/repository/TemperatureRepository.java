package com.hivesense.hivesense.repository;

import com.hivesense.hivesense.entity.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
}