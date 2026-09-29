package com.hivesense.hivesense.service;

import com.hivesense.hivesense.entity.Device;
import com.hivesense.hivesense.entity.User;
import com.hivesense.hivesense.repository.DeviceRepository;
import com.hivesense.hivesense.repository.TemperatureRepository;
import com.hivesense.hivesense.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hivesense.hivesense.service.JwtService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final TemperatureRepository temperatureRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
        UserRepository userRepository,
        DeviceRepository deviceRepository,
        TemperatureRepository temperatureRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {

    this.userRepository = userRepository;
    this.deviceRepository = deviceRepository;
    this.temperatureRepository = temperatureRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(String login, String email, String password) {

        if (userRepository.findByLogin(login).isPresent()) {
            throw new RuntimeException("Login jest już zajęty");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email jest już zajęty");
        }

        User user = new User();

        user.setLogin(login);
        user.setEmail(email);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        String apiKey = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 12);

        user.setApiKey(apiKey);

        return userRepository.save(user);
    }

public String loginUser(String login, String password) {

    User user = userRepository.findByLogin(login).orElse(null);

    if (user == null) {
        throw new RuntimeException("Nieprawidłowy login lub hasło");
    }

    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new RuntimeException("Nieprawidłowy login lub hasło");
    }

    return jwtService.generateToken(
            user.getId(),
            user.getLogin()
    );
}

   @Transactional
public void deleteUser(String login) {

    User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));

    Long userId = user.getId();

    List<Device> devices = deviceRepository.findByUserId(userId);

    for (Device device : devices) {
        temperatureRepository.deleteByDeviceId(device.getId());
    }

    deviceRepository.deleteAll(devices);

    userRepository.delete(user);
}



    public void changePassword(
        String login,
        String oldPassword,
        String newPassword
) {

    User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("Nieprawidłowy login lub hasło"));

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        throw new RuntimeException("Nieprawidłowy login lub hasło");
    }

    String hashedPassword = passwordEncoder.encode(newPassword);

    user.setPassword(hashedPassword);

    userRepository.save(user);
}

public User getUserByLogin(String login) {

    return userRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));
}
}