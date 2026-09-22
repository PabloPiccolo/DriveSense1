package com.hivesense.hivesense.service;

import com.hivesense.hivesense.entity.Temperature;
import com.hivesense.hivesense.repository.TemperatureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemperatureService {

    private final TemperatureRepository temperatureRepository;

    public TemperatureService(TemperatureRepository temperatureRepository) {
        this.temperatureRepository = temperatureRepository;
    }

    public List<Temperature> getAllTemperatures() {
        return temperatureRepository.findAll();
    }

    public Temperature saveTemperature(Temperature temperature) {
        return temperatureRepository.save(temperature);
    }
}