package com.nimbleways.springboilerplate.services.order.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.UnsupportedProductTypeException;
import com.nimbleways.springboilerplate.services.order.OrderService;
import com.nimbleways.springboilerplate.services.product.processors.ExpirableProductProcessor;
import com.nimbleways.springboilerplate.services.product.processors.NormalProductProcessor;
import com.nimbleways.springboilerplate.services.product.processors.ProductProcessor;
import com.nimbleways.springboilerplate.services.product.processors.SeasonalProductProcessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
  private final Map<String, ProductProcessor> processors = new HashMap<>();

    public OrderServiceImpl(NormalProductProcessor normalProcessor,
                            SeasonalProductProcessor seasonalProcessor,
                            ExpirableProductProcessor expirableProcessor) {
        processors.put("NORMAL", normalProcessor);
        processors.put("SEASONAL", seasonalProcessor);
        processors.put("EXPIRABLE", expirableProcessor);
    }

    public void processOrder(Order order) {
        Set<Product> products = order.getItems();
        for (Product product : products) {
            ProductProcessor processor = processors.get(product.getType());
            if (processor == null) {
                throw new UnsupportedProductTypeException(product.getType());
            }
            processor.process(product);
        }
    }

}