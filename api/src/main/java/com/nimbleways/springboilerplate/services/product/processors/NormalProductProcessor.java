package com.nimbleways.springboilerplate.services.product.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NormalProductProcessor implements ProductProcessor {

    private  final ProductRepository productRepository;


    private  final NotificationService notificationService;

    public NormalProductProcessor(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void process(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notificationService.sendDelayNotification(leadTime, product.getName());
            }
        }
    }
}