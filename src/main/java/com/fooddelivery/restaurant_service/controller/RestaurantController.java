package com.fooddelivery.restaurant_service.controller;
import com.fooddelivery.restaurant_service.dto.RestaurantDTO;
import com.fooddelivery.restaurant_service.service.RestaurantService;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
// Create a new restaurant
//@PreAuthorize("hasRole('ADMIN')")
//@PostMapping
//public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
//        RestaurantDTO createdRestaurant = restaurantService.addRestaurant(restaurantDTO);
//    return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
//}
// Get a restaurant by ID
//@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
//@GetMapping("/{id}") public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
//        RestaurantDTO restaurant = restaurantService.getRestaurantById(id);
//        return ResponseEntity.ok(restaurant); }

    //Update a restaurant w restaurent ya3mel update kn lrou7o
    @PreAuthorize("hasRole('RESTAURANT')")
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDTO restaurantDTO,
            Authentication authentication) {

        // authentication.getName() may be an email (token subject). Lookup restaurant by email
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
}


// Delete a restaurant
    //@PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {restaurantService.deleteRestaurant(id);
//        return ResponseEntity.noContent().build(); }

//@GetMapping
//@PreAuthorize("hasRole('ADMIN') or hasRole('USER')") // optional: allow admin + users
//public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
//    List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
//    return ResponseEntity.ok(restaurants);
//}}
