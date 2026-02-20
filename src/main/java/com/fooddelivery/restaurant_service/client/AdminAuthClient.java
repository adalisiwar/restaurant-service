package com.fooddelivery.restaurant_service.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "adminAuthClient", url = "http://localhost:8081")
public interface AdminAuthClient {

    @PostMapping(value="/api/admin/auth/login", consumes="application/json")
    LoginResponse login(@RequestBody LoginRequest request);

    public class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
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
    }

    public class LoginResponse {
        @NotBlank(message = "Token is required")
        private String token;

        @NotBlank(message = "Email is required")
        private String email;


        public LoginResponse(String token, String username) {
            this.token = token;
            this.email = username;
        }

        // Getters and setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setEmail(String em) {
            this.email = em;
        }

        public String getEmail() {
            return email;
        }

    }
}
