package com.fooddelivery.restaurant_service.controller;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fooddelivery.restaurant_service.client.AdminAuthClient;

@RestController
@RequestMapping("/api/restaurant/auth")
public class AuthController {

    @Autowired
    private AdminAuthClient adminAuthClient;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminAuthClient.LoginRequest loginRequest) {
        try {
            AdminAuthClient.LoginResponse response = adminAuthClient.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (FeignException e) {
            int status = e.status() > 0 ? e.status() : 500;
            return ResponseEntity.status(status)
                    .body("Auth service error: " + e.getMessage());
        }
    }

}



//@Autowired
//private JwtTokenProvider tokenProvider;

//@Autowired
//private RestaurantRepository restaurantRepository;

//@Autowired
//private PasswordEncoder passwordEncoder;

//@PostMapping("/login")
//public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

    //Optional<Restaurant> optionalRestaurant = restaurantRepository.findByName(loginRequest.getName());
    //if (optionalRestaurant.isEmpty()) {
        //return ResponseEntity.badRequest()
                //.body(new ErrorResponse("Invalid credentials"));
    //}

    //Restaurant restaurant = optionalRestaurant.get();

    //if (!passwordEncoder.matches(loginRequest.getPassword(), restaurant.getPassword())) {
        //return ResponseEntity.badRequest()
               // .body(new ErrorResponse("Invalid credentials"));
   // }

    //Map<String, Object> claims = new HashMap<>();
    //claims.put("role", "RESTAURANT"); // you can add more roles if needed

    //String token = tokenProvider.generateToken(restaurant.getName(), claims);

    //return ResponseEntity.ok(new LoginResponse(token, restaurant.getName()));
//}
