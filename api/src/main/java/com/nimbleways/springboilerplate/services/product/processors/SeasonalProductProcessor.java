package com.nimbleways.springboilerplate.services.product.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service

public class SeasonalProductProcessor implements ProductProcessor {
    private final  ProductRepository productRepository;

    private final NotificationService notificationService;

    public SeasonalProductProcessor(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void process(Product product) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate()) && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate()) || product.getSeasonStartDate().isAfter(now)) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }
}