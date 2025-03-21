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
public class ExpirableProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductProcessor expirableProcessor;

    @Test
    public void process_withAvailableStockAndNotExpired_decrementsStock() {
        // GIVEN
        Product product = new Product(null, 15, 10, "EXPIRABLE", "Butter", LocalDate.now().plusDays(5), null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        expirableProcessor.process(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(0)).sendExpirationNotification(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void process_withAvailableStockButExpired_sendsExpirationNotificationAndSetsStockToZero() {
        // GIVEN
        Product product = new Product(null, 15, 10, "EXPIRABLE", "Milk", LocalDate.now().minusDays(1), null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        expirableProcessor.process(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification("Milk", product.getExpiryDate());
        Mockito.verifyNoMoreInteractions(notificationService);
    }



}