package org.devoir.deliveryservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.devoir.deliveryservice.client.GoogleMapsClient;
import org.devoir.deliveryservice.model.Delivery;
import org.devoir.deliveryservice.model.DeliveryStatus;
import org.devoir.deliveryservice.model.Driver;
import org.devoir.deliveryservice.repository.DeliveryRepository;
import org.devoir.deliveryservice.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    private final GoogleMapsClient googleMapsClient;

    @Transactional
    public Mono<Delivery> createDelivery(Delivery delivery) {
        List<Driver> availableDrivers = driverRepository.findByAvailableTrue();
        if (availableDrivers.isEmpty()) {
            return Mono.error(new RuntimeException("Aucun livreur disponible"));
        }

        Driver driver = availableDrivers.get(0);
        delivery.setDriverId(driver.getId());
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        return googleMapsClient.getEstimatedTime(
                        delivery.getPickupAddress(),
                        delivery.getDeliveryAddress()
                )
                .map(estimatedTime -> {
                    delivery.setEstimatedTime(estimatedTime);
                    driver.setAvailable(false);
                    driverRepository.save(driver);
                    return deliveryRepository.save(delivery);
                });
    }

    public Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));
    }

    public Delivery getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));
    }

    @Transactional
    public Delivery updateLocation(Long id, Double latitude, Double longitude) {
        Delivery delivery = getDelivery(id);
        delivery.setCurrentLatitude(latitude);
        delivery.setCurrentLongitude(longitude);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = getDelivery(id);
        delivery.setStatus(status);

        if (status == DeliveryStatus.PICKED_UP) {
            delivery.setPickupTime(LocalDateTime.now());
        } else if (status == DeliveryStatus.DELIVERED) {
            delivery.setDeliveryTime(LocalDateTime.now());
            Driver driver = driverRepository.findById(delivery.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));
            driver.setAvailable(true);
            driverRepository.save(driver);
        }

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getDriverDeliveries(Long driverId) {
        return deliveryRepository.findByDriverId(driverId);
    }
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll(); // ou la méthode adaptée à ton repository
    }

}
