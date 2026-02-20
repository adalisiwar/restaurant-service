package com.fooddelivery.restaurant_service.repository;

import com.fooddelivery.restaurant_service.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    Optional<Restaurant> findByName(String name);
    Optional<Restaurant> findByEmail(String email);

}
