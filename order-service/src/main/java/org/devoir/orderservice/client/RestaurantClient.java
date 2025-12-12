package org.devoir.orderservice.client;

import org.devoir.orderservice.dto.MenuItemDTO;
import org.devoir.orderservice.dto.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// IMPORTANT: Ajouter url directement
@FeignClient(
        name = "restaurant-service",
        url = "${feign.client.config.restaurant-service.url:http://localhost:8081}"
)
public interface RestaurantClient {

    @GetMapping("/api/restaurants/{id}")
    RestaurantDTO getRestaurant(@PathVariable("id") Long id);

    @GetMapping("/api/restaurants/{restaurantId}/menu/{menuItemId}")
    MenuItemDTO getMenuItem(
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("menuItemId") Long menuItemId
    );
}