package com.hivesense.hivesense.repository;

import com.hivesense.hivesense.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByUserIdAndDeviceName(
            Long userId,
            String deviceName
    );

    List<Device> findByUserId(Long userId);
}