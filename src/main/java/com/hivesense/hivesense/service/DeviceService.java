package com.hivesense.hivesense.service;

import com.hivesense.hivesense.entity.Device;
import com.hivesense.hivesense.repository.DeviceRepository;
import com.hivesense.hivesense.repository.TemperatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final TemperatureRepository temperatureRepository;

    public DeviceService(
            DeviceRepository deviceRepository,
            TemperatureRepository temperatureRepository
    ) {
        this.deviceRepository = deviceRepository;
        this.temperatureRepository = temperatureRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public List<Device> getDevicesForUser(Long userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteDevice(Long userId, String deviceName) {

        Device device = deviceRepository
                .findByUserIdAndDeviceName(userId, deviceName)
                .orElseThrow(() -> new RuntimeException("Urządzenie nie istnieje"));

        temperatureRepository.deleteByDeviceId(device.getId());

        deviceRepository.delete(device);
    }
}