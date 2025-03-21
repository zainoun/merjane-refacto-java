package com.nimbleways.springboilerplate.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private ProductRepository productRepository;

        @BeforeEach
        public void setUp() {
                orderRepository.deleteAll();
                productRepository.deleteAll();
        }

        @Test
        public void processOrder_withValidOrderId_returnsOkAndOrderId() throws Exception {
                // GIVEN
                List<Product> products = createProducts();
                Set<Product> orderItems = new HashSet<>(products);
                Order order = createOrder(orderItems);
                productRepository.saveAll(products);
                Order savedOrder = orderRepository.save(order);

                // WHEN
                mockMvc.perform(post("/orders/{orderId}/processOrder", savedOrder.getId())
                                .contentType("application/json"))
                        // THEN
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(savedOrder.getId()));

                Order resultOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
                assertEquals(savedOrder.getId(), resultOrder.getId());
        }

        @Test
        public void processOrder_withInvalidOrderId_returnsNotFound() throws Exception {
                // GIVEN
                Long invalidOrderId = 999L;

                // WHEN
                mockMvc.perform(post("/orders/{orderId}/processOrder", invalidOrderId)
                                .contentType("application/json"))
                        // THEN
                        .andExpect(status().isNotFound());
        }

        private static Order createOrder(Set<Product> products) {
                Order order = new Order();
                order.setItems(products);
                return order;
        }

        private static List<Product> createProducts() {
                return List.of(
                        new Product(null, 15, 30, "NORMAL", "USB Cable", null, null, null),
                        new Product(null, 10, 0, "NORMAL", "USB Dongle", null, null, null),
                        new Product(null, 15, 30, "EXPIRABLE", "Butter", LocalDate.now().plusDays(26), null, null),
                        new Product(null, 90, 6, "EXPIRABLE", "Milk", LocalDate.now().minusDays(2), null, null),
                        new Product(null, 15, 30, "SEASONAL", "Watermelon", null, LocalDate.now().minusDays(2), LocalDate.now().plusDays(58)),
                        new Product(null, 15, 0, "SEASONAL", "Grapes", null, LocalDate.now().plusDays(180), LocalDate.now().plusDays(240))
                );
        }



}