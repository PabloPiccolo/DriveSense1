package com.hivesense.hivesense.controller;

import com.hivesense.hivesense.entity.User;
import com.hivesense.hivesense.dto.LoginRequest;
import com.hivesense.hivesense.dto.RegisterRequest;
import com.hivesense.hivesense.service.UserService;
import com.hivesense.hivesense.dto.ChangePasswordRequest;
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
        @RequestBody RegisterRequest request
) {

    try {

        User user = userService.registerUser(
                request.getLogin(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(user);

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}

    
    @PostMapping("/login")
public ResponseEntity<?> loginUser(
        @RequestBody LoginRequest request
) {

    try {

        String token = userService.loginUser(
                request.getLogin(),
                request.getPassword()
        );

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

    @DeleteMapping("/me")
public ResponseEntity<?> deleteCurrentUser(
        Authentication authentication
) {

    try {

        String login = authentication.getName();

        userService.deleteUser(login);

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
        @RequestBody ChangePasswordRequest request
) {

    try {

        String login = authentication.getName();

        userService.changePassword(
                login,
                request.getOldPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Hasło zostało zmienione");

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
}


}