package com.example.login_second;

import java.io.Serializable;

public class LoginDetails implements Serializable {
    private String username;
    private String password;

    public LoginDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
