package com.nimbleways.springboilerplate.repositories;

import com.nimbleways.springboilerplate.entities.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findById(Long productId);

    Optional<Product> findFirstByName(String name);
}
