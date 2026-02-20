package com.fooddelivery.restaurant_service.service;

import com.fooddelivery.restaurant_service.dto.MenuItemDTO;

import java.util.List;

public interface MenuItemService {

    MenuItemDTO addMenuItem(MenuItemDTO dto);

    MenuItemDTO updateMenuItem(Long id, MenuItemDTO dto);

    void deleteMenuItem(Long id);

    List<MenuItemDTO> getMenuItemsByRestaurant(Long restaurantId);
}
