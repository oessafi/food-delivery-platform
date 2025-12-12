package org.devoir.orderservice.service;

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
    // Commentez temporairement les clients Feign
    // private final RestaurantClient restaurantClient;
    // private final DeliveryClient deliveryClient;

    public Order createOrder(Order order) {
        System.out.println("📥 Ordre reçu: " + order);
        System.out.println("📦 Items: " + order.getOrderItems().size());

        // Calcul simple du prix total SANS appeler les services
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()) {
            // Prix par défaut si non fourni
            if (item.getPrice() == null) {
                item.setPrice(BigDecimal.valueOf(80.00));
            }
            if (item.getMenuItemName() == null) {
                item.setMenuItemName("Item " + item.getMenuItemId());
            }

            totalPrice = totalPrice.add(
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        // Créer la commande
        order.setTotalPrice(totalPrice);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryTime(LocalDateTime.now().plusHours(1));

        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Commande créée: " + savedOrder.getId());

        return savedOrder;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrder(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}