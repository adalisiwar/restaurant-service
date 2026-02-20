package com.fooddelivery.restaurant_service.controller;

import com.fooddelivery.restaurant_service.dto.MenuItemDTO;
import com.fooddelivery.restaurant_service.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    // Add a new menu item
    @PostMapping
    public ResponseEntity<MenuItemDTO> addMenuItem(@RequestBody MenuItemDTO dto) {
        MenuItemDTO created = menuItemService.addMenuItem(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Update a menu item
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Long id,
                                                      @RequestBody MenuItemDTO dto) {
        MenuItemDTO updated = menuItemService.updateMenuItem(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Delete a menu item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    // Get all menu items by restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        List<MenuItemDTO> menuItems = menuItemService.getMenuItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(menuItems);
    }
}
