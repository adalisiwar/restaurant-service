package com.fooddelivery.restaurant_service.controller;
import com.fooddelivery.restaurant_service.dto.RestaurantDTO;
import com.fooddelivery.restaurant_service.service.RestaurantService;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO createdRestaurant = restaurantService.addRestaurant(restaurantDTO);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDTO restaurantDTO,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("Updating restaurant {} - unauthenticated request (internal sync from admin)", id);
            RestaurantDTO updated = restaurantService.updateRestaurant(id, restaurantDTO);
            return ResponseEntity.ok(updated);
        }

        if ("anonymousUser".equals(authentication.getName())) {
            logger.debug("Updating restaurant {} - anonymous request (internal sync from admin)", id);
            RestaurantDTO updated = restaurantService.updateRestaurant(id, restaurantDTO);
            return ResponseEntity.ok(updated);
        }

        String subject = authentication.getName();
        logger.debug("updateRestaurant called: pathId={}, tokenSubject={}", id, subject);
        try {
            RestaurantDTO restaurantFromToken = restaurantService.getRestaurantByEmail(subject);
            logger.debug("found restaurant from token: id={}, email={}", restaurantFromToken.getId(), restaurantFromToken.getEmail());
            if (!restaurantFromToken.getId().equals(id)) {
                logger.warn("Forbidden: token restaurant id {} does not match path id {}", restaurantFromToken.getId(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            RestaurantDTO updated = restaurantService.updateRestaurant(id, restaurantDTO);
            logger.info("Restaurant {} updated by itself", id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            logger.warn("Forbidden: could not find restaurant for subject {}: {}", subject, ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}


