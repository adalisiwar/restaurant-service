package com.fooddelivery.restaurant_service.service;

import com.fooddelivery.restaurant_service.client.AdminRestoClient;
import com.fooddelivery.restaurant_service.dto.RestaurantDTO;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantRepository restaurantRepository;
    private final AdminRestoClient adminRestoClient;

    // Add a new restaurant
    @Override
    public RestaurantDTO addRestaurant(RestaurantDTO dto) {
        // Convert DTO → Entity
        Restaurant restaurant = Restaurant.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        RestaurantDTO savedDTO = mapToDTO(saved);

        // Sync creation to admin service
        syncCreateWithAdminService(savedDTO);

        return savedDTO;
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
            restaurant.setPassword(dto.getPassword());
        }

        Restaurant updated = restaurantRepository.save(restaurant);
        RestaurantDTO updatedDTO = mapToDTO(updated);

        // Sync update to admin service
        syncUpdateWithAdminService(id, updatedDTO);

        return updatedDTO;
    }

    // Delete a restaurant by id
    @Override
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RuntimeException("Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);

        // Sync deletion to admin service
        syncDeleteWithAdminService(id);
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

    // Sync restaurant creation to admin service
    private void syncCreateWithAdminService(RestaurantDTO dto) {
        try {
            adminRestoClient.createRestaurant(dto);
            logger.info("Successfully synced restaurant {} creation to admin service", dto.getId());
        } catch (Exception e) {
            logger.error("Failed to sync restaurant {} creation to admin service: {}", dto.getId(), e.getMessage(), e);
            // Don't throw exception - restaurant service should not fail if admin service is down
        }
    }

    // Sync restaurant update to admin service
    private void syncUpdateWithAdminService(Long id, RestaurantDTO dto) {
        try {
            adminRestoClient.updateRestaurant(id, dto);
            logger.info("Successfully synced restaurant {} update to admin service", id);
        } catch (Exception e) {
            logger.error("Failed to sync restaurant {} update to admin service: {}", id, e.getMessage(), e);
            // Don't throw exception - restaurant service should not fail if admin service is down
        }
    }

    // Sync restaurant deletion to admin service
    private void syncDeleteWithAdminService(Long id) {
        try {
            adminRestoClient.deleteRestaurant(id);
            logger.info("Successfully synced restaurant {} deletion to admin service", id);
        } catch (Exception e) {
            logger.error("Failed to sync restaurant {} deletion to admin service: {}", id, e.getMessage(), e);
            // Don't throw exception - restaurant service should not fail if admin service is down
        }
    }

}

