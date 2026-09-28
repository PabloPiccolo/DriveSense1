package com.hivesense.hivesense.controller;

import com.hivesense.hivesense.entity.Device;
import com.hivesense.hivesense.entity.User;
import com.hivesense.hivesense.repository.UserRepository;
import com.hivesense.hivesense.service.DeviceService;
import org.springframework.web.bind.annotation.*;
import com.hivesense.hivesense.entity.Temperature;
import com.hivesense.hivesense.service.TemperatureService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/app/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final UserRepository userRepository;
    private final TemperatureService temperatureService;

    public DeviceController(
        DeviceService deviceService,
        UserRepository userRepository,
        TemperatureService temperatureService
) {
    this.deviceService = deviceService;
    this.userRepository = userRepository;
    this.temperatureService = temperatureService;
}

    @GetMapping
    public List<Device> getDevices(
            @RequestParam String apiKey
    ) {

        User user = userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API key"));

        return deviceService.getDevicesForUser(user.getId());
    }

    @GetMapping("/{deviceId}/temperatures")
public List<Temperature> getTemperatures(
        @PathVariable Long deviceId,
        @RequestParam String date
) {

    LocalDate parsedDate = LocalDate.parse(date);

    return temperatureService.getTemperaturesByDeviceIdAndDate(
            deviceId,
            parsedDate
    );
}



@DeleteMapping("/{deviceName}")
public String deleteDevice(
        @RequestParam String apiKey,
        @PathVariable String deviceName
) {

    User user = userRepository.findByApiKey(apiKey)
            .orElseThrow(() -> new RuntimeException("Invalid API key"));

    deviceService.deleteDevice(user.getId(), deviceName);

    return "Urządzenie zostało usunięte";
}
}