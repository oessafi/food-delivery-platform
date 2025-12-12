package org.devoir.orderservice.service;

import org.devoir.orderservice.client.RestaurantClient; // Import nécessaire
import org.devoir.orderservice.dto.MenuItemDTO;       // Import nécessaire
import org.devoir.orderservice.model.Order;
import org.devoir.orderservice.model.OrderItem;
import org.devoir.orderservice.model.OrderStatus;
import org.devoir.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient; // CORRECTION : Décommenté

    public Order createOrder(Order order) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        // CORRECTION : Appel réel au microservice Restaurant
        for (OrderItem item : order.getOrderItems()) {
            MenuItemDTO menuItem = restaurantClient.getMenuItem(item.getMenuItemId());

            // Mise à jour des infos avec les vraies données du restaurant
            item.setPrice(menuItem.getPrice());
            item.setMenuItemName(menuItem.getName());

            totalPrice = totalPrice.add(
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        order.setTotalPrice(totalPrice);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryTime(LocalDateTime.now().plusHours(1));

        return orderRepository.save(order);
    }

    // ... gardez les autres méthodes (getOrder, getUserOrders, etc.) telles quelles
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }
    public List<Order> getUserOrders(Long userId) { return orderRepository.findByUserId(userId); }
    public List<Order> getAllOrders() { return orderRepository.findAll(); }
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrder(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}