package com.hivesense.hivesense.controller;

import com.hivesense.hivesense.entity.User;
import com.hivesense.hivesense.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
public ResponseEntity<?> getCurrentUser(
        Authentication authentication
) {

    String login = authentication.getName();

    User user = userService.getUserByLogin(login);

    return ResponseEntity.ok(user);
}

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam String login,
            @RequestParam String email,
            @RequestParam String password
    ) {

        try {
            User user = userService.registerUser(login, email, password);
            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
public ResponseEntity<?> loginUser(
        @RequestParam String login,
        @RequestParam String password
) {

    try {
        String token = userService.loginUser(login, password);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "token", token
                )
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        try {
            userService.deleteUser(id);

            return ResponseEntity.ok("Konto zostało usunięte");

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

   @PutMapping("/change-password")
public ResponseEntity<?> changePassword(
        Authentication authentication,
        @RequestParam String oldPassword,
        @RequestParam String newPassword
) {

    try {

        String login = authentication.getName();

        userService.changePassword(
                login,
                oldPassword,
                newPassword
        );

        return ResponseEntity.ok("Hasło zostało zmienione");

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
}
}