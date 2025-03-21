package com.nimbleways.springboilerplate.services.product.processors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class SeasonalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductProcessor seasonalProcessor;

    @Test
    public void process_withAvailableStockAndInSeason_decrementsStock() {
        // GIVEN
        Product product = new Product(null, 15, 30, "SEASONAL", "Watermelon", null,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        seasonalProcessor.process(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(0)).sendOutOfStockNotification(Mockito.anyString());
        verify(notificationService, times(0)).sendDelayNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void process_withNoStockAndInSeason_sendsDelayNotification() {
        // GIVEN
        Product product = new Product(null, 15, 0, "SEASONAL", "Apple", null,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        seasonalProcessor.process(product);

        // THEN
        verify(productRepository, times(0)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(15, "Apple");
        verify(notificationService, times(0)).sendOutOfStockNotification(Mockito.anyString());
    }




}