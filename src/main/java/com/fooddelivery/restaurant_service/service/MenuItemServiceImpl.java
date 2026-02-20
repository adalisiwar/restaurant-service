package com.fooddelivery.restaurant_service.service;

import com.fooddelivery.restaurant_service.dto.MenuItemDTO;
import com.fooddelivery.restaurant_service.model.MenuItem;
import com.fooddelivery.restaurant_service.model.Restaurant;
import com.fooddelivery.restaurant_service.repository.MenuItemRepository;
import com.fooddelivery.restaurant_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    // ================== ADD ==================
    @Override
    public MenuItemDTO addMenuItem(MenuItemDTO dto) {

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException(
                        "Restaurant not found with id: " + dto.getRestaurantId()
                ));

        MenuItem menuItem = MenuItem.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .available(dto.getAvailable())
                .restaurant(restaurant)
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);
        return toDTO(saved);
    }

    // ================== UPDATE ==================
    @Override
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO dto) {

        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setAvailable(dto.getAvailable());

        MenuItem updated = menuItemRepository.save(existing);
        return toDTO(updated);
    }

    // ================== DELETE ==================
    @Override
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    // ================== GET BY RESTAURANT ==================
    @Override
    public List<MenuItemDTO> getMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ================== DTO MAPPERS ==================
    private MenuItemDTO toDTO(MenuItem entity) {
        return MenuItemDTO.builder()
                .id(entity.getId())
                .restaurantId(entity.getRestaurant().getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .available(entity.getAvailable())
                .build();
    }
}
