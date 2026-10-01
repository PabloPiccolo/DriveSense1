package com.hivesense.hivesense.dto;

public class LoginResponse {

    private Long id;
    private String login;
    private String email;
    private String apiKey;
    private String token;

    public LoginResponse(
            Long id,
            String login,
            String email,
            String apiKey,
            String token
    ) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.apiKey = apiKey;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getToken() {
        return token;
    }
}