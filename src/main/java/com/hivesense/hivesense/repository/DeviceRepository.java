package com.hivesense.hivesense.repository;

import com.hivesense.hivesense.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}