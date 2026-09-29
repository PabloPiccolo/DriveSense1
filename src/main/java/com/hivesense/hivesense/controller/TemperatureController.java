package com.hivesense.hivesense.controller;

import com.hivesense.hivesense.dto.TemperatureRequest;
import com.hivesense.hivesense.entity.Temperature;
import com.hivesense.hivesense.service.TemperatureService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/temperatures")
public class TemperatureController {

    private final TemperatureService temperatureService;

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping
    public List<Temperature> getAllTemperatures() {
        return temperatureService.getAllTemperatures();
    }

    @GetMapping("/device/{deviceId}")
    public List<Temperature> getTemperaturesByDeviceId(
            @PathVariable Long deviceId
    ) {
        return temperatureService.getTemperaturesByDeviceId(deviceId);
    }

    @PostMapping
    public Temperature saveTemperature(
            @Valid @RequestBody TemperatureRequest request
    ) {

        return temperatureService.saveTemperature(
                request.getApiKey(),
                request.getDeviceName(),
                request.getTemperature()
        );
    }
}