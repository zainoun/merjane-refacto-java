package com.nimbleways.springboilerplate.services.order.implementations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.UnsupportedProductTypeException;
import com.nimbleways.springboilerplate.services.product.processors.ExpirableProductProcessor;
import com.nimbleways.springboilerplate.services.product.processors.NormalProductProcessor;
import com.nimbleways.springboilerplate.services.product.processors.SeasonalProductProcessor;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class OrderServiceImplTest {

    @Mock
    private NormalProductProcessor normalProcessor;

    @Mock
    private SeasonalProductProcessor seasonalProcessor;

    @Mock
    private ExpirableProductProcessor expirableProcessor;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    public void setUp() {
        order = new Order();
    }

    @Test
    public void processOrder_withNormalProduct_callsNormalProcessor() {
        // GIVEN
        Product product = new Product(null, 15, 30, "NORMAL", "USB Cable", null, null, null);
        order.setItems(Set.of(product));

        // WHEN
        orderService.processOrder(order);

        // THEN
        verify(normalProcessor, times(1)).process(product);
        verify(seasonalProcessor, times(0)).process(Mockito.any());
        verify(expirableProcessor, times(0)).process(Mockito.any());
    }

    @Test
    public void processOrder_withSeasonalProduct_callsSeasonalProcessor() {
        // GIVEN
        Product product = new Product(null, 15, 30, "SEASONAL", "Watermelon", null,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(58));
        order.setItems(Set.of(product));

        // WHEN
        orderService.processOrder(order);

        // THEN
        verify(seasonalProcessor, times(1)).process(product);
        verify(normalProcessor, times(0)).process(Mockito.any());
        verify(expirableProcessor, times(0)).process(Mockito.any());
    }

    @Test
    public void processOrder_withUnsupportedProductType_throwsException() {
        // GIVEN
        Product product = new Product(null, 15, 30, "UNKNOWN", "Invalid Product", null, null, null);
        order.setItems(Set.of(product));

        // WHEN & THEN
        assertThrows(UnsupportedProductTypeException.class, () -> orderService.processOrder(order));
    }

    @Test
    public void processOrder_withMultipleProducts_callsAppropriateProcessors() {
        // GIVEN
        Product normalProduct = new Product(null, 15, 30, "NORMAL", "USB Cable", null, null, null);
        Product seasonalProduct = new Product(null, 15, 0, "SEASONAL", "Apple", null,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20));
        Product expirableProduct = new Product(null, 15, 10, "EXPIRABLE", "Butter", LocalDate.now().plusDays(5), null, null);
        order.setItems(Set.of(normalProduct, seasonalProduct, expirableProduct));

        // WHEN
        orderService.processOrder(order);

        // THEN
        verify(normalProcessor, times(1)).process(normalProduct);
        verify(seasonalProcessor, times(1)).process(seasonalProduct);
        verify(expirableProcessor, times(1)).process(expirableProduct);
    }
}