package com.fooddelivery.restaurant_service.client;

import com.fooddelivery.restaurant_service.dto.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "admin-service",
        url = "http://localhost:8081"
)
public interface AdminRestoClient {

    @PostMapping("/api/restaurants/create")
    ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO);

    @GetMapping("/api/restaurants/{id}")
    ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable("id") Long id);

    @DeleteMapping("/api/restaurants/{id}")
    ResponseEntity<Void> deleteRestaurant(@PathVariable("id") Long id);

    @GetMapping("/api/restaurants")
    ResponseEntity<List<RestaurantDTO>> getAllRestaurants();
}
