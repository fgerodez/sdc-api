package com.sdc.fgerodez.product.domain;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ProductRepository extends Repository<Product, Long> {
    void deleteById(Long id);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Iterable<Product> findAll();
}
