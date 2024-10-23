package com.alten.fgerodez.product.domain;

import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {
    void deleteById(Long id);

    Product findById(Long id);

    Product save(Product product);

    Iterable<Product> findAll();
}
