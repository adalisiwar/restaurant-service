package com.fooddelivery.restaurant_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String name; // restaurant name
    private String password;
}
