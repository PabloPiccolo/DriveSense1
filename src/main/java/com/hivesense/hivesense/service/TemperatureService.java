package com.hivesense.hivesense.service;

import com.hivesense.hivesense.entity.Device;
import com.hivesense.hivesense.entity.Temperature;
import com.hivesense.hivesense.entity.User;
import com.hivesense.hivesense.repository.DeviceRepository;
import com.hivesense.hivesense.repository.TemperatureRepository;
import com.hivesense.hivesense.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemperatureService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final TemperatureRepository temperatureRepository;

    public TemperatureService(
            UserRepository userRepository,
            DeviceRepository deviceRepository,
            TemperatureRepository temperatureRepository
    ) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.temperatureRepository = temperatureRepository;
    }

    public List<Temperature> getAllTemperatures() {
        return temperatureRepository.findAll();
    }

    public Temperature saveTemperature(
            String apiKey,
            String deviceName,
            Double temperature
    ) {

        // 1. Znajdź użytkownika po apiKey
        User user = userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Nieprawidłowy apiKey"));

        // 2. Znajdź urządzenie
        Device device = deviceRepository
                .findByUserIdAndDeviceName(user.getId(), deviceName)
                .orElseGet(() -> {

                    // 3. Jeżeli urządzenia nie ma, utwórz nowe
                    Device newDevice = new Device();

                    newDevice.setUserId(user.getId());
                    newDevice.setDeviceName(deviceName);

                    return deviceRepository.save(newDevice);
                });

        // 4. Utwórz pomiar
        Temperature newTemperature = new Temperature();

        newTemperature.setDeviceId(device.getId());
        newTemperature.setTemperature(temperature);
        newTemperature.setMeasuredAt(LocalDateTime.now());

        // 5. Zapisz pomiar
        return temperatureRepository.save(newTemperature);
    }

    public List<Temperature> getTemperaturesByDeviceId(Long deviceId) {
        return temperatureRepository.findByDeviceId(deviceId);
    }

    public List<Temperature> getTemperaturesByDeviceIdAndDate(
        Long deviceId,
        LocalDate date
) {

    LocalDateTime start = date.atStartOfDay();
    LocalDateTime end = date.plusDays(1).atStartOfDay();

    return temperatureRepository.findByDeviceIdAndMeasuredAtBetween(
            deviceId,
            start,
            end
    );
}
}