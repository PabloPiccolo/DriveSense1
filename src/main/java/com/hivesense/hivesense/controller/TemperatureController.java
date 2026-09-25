package com.hivesense.hivesense.controller;

import com.hivesense.hivesense.entity.Temperature;
import com.hivesense.hivesense.service.TemperatureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public Temperature saveTemperature(@RequestBody Map<String, Object> data) {

        String apiKey = (String) data.get("apiKey");
        String deviceName = (String) data.get("deviceName");
        Double temperature = ((Number) data.get("temperature")).doubleValue();

        return temperatureService.saveTemperature(
                apiKey,
                deviceName,
                temperature
        );
    }


    
}