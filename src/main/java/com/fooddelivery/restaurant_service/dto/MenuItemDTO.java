package com.fooddelivery.restaurant_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Builder

public class MenuItemDTO {
    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private Double price;
    private Boolean available;
}
