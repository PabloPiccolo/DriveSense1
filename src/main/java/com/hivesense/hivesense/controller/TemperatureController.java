package com.hivesense.hivesense.controller;

import com.hivesense.hivesense.entity.Temperature;
import com.hivesense.hivesense.service.TemperatureService;
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

    @PostMapping
    public Temperature saveTemperature(@RequestBody Temperature temperature) {
        return temperatureService.saveTemperature(temperature);
    }
}