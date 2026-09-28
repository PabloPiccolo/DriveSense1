package com.hivesense.hivesense.repository;

import com.hivesense.hivesense.entity.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {

    List<Temperature> findByDeviceId(Long deviceId);

    List<Temperature> findByDeviceIdAndMeasuredAtBetween(
            Long deviceId,
            LocalDateTime start,
            LocalDateTime end
    );

    void deleteByDeviceId(Long deviceId);
}