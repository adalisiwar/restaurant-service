package com.fooddelivery.restaurant_service.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDTO {

    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // only for input, do NOT return in responses
}
