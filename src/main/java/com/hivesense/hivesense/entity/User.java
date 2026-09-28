// package com.hivesense.hivesense.entity;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;

// @Entity                         // jestem encją JPA

// @Table(name = "users")         // odpowiadam tabeli users

// public class User {

//     @Id                         // klucz główny
//     private Long id;

//     private String login;       // kolumna login
//     private String email;       // kolumna email
//     private String password;    // kolumna password

//     @Column(name = "`apiKey`")  // dokładnie kolumna apiKey
//     private String apiKey;
// }


package com.hivesense.hivesense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

   @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String login;

    private String email;

    @JsonIgnore
private String password;
    

    @Column(name = "`apiKey`")
private String apiKey;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}