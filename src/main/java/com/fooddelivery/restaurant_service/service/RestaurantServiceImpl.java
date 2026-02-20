package com.fooddelivery.restaurant_service.service;

import com.fooddelivery.restaurant_service.dto.RestaurantDTO;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    // Add a new restaurant
    @Override
    public RestaurantDTO addRestaurant(RestaurantDTO dto) {
        // Convert DTO → Entity
        Restaurant restaurant = Restaurant.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword())) // encode password
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToDTO(saved);
    }

    // Update an existing restaurant
    @Override
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        if (dto.getName() != null) restaurant.setName(dto.getName());
        if (dto.getAddress() != null) restaurant.setAddress(dto.getAddress());
        if (dto.getPhone() != null) restaurant.setPhone(dto.getPhone());
        if (dto.getEmail() != null) restaurant.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            restaurant.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Restaurant updated = restaurantRepository.save(restaurant);
        return mapToDTO(updated);
    }

    // Delete a restaurant by id
    @Override
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);
    }

    // Get all restaurants
    @Override
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get a restaurant by id
    @Override
    public RestaurantDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return mapToDTO(restaurant);
    }

    @Override
    public RestaurantDTO getRestaurantByEmail(String email) {
        Restaurant restaurant = restaurantRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with email: " + email));
        return mapToDTO(restaurant);
    }

    // Helper: map Restaurant → DTO
    private RestaurantDTO mapToDTO(Restaurant restaurant) {
        return RestaurantDTO.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .build();
    }


}
