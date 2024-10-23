package com.sdc.fgerodez.product.infrastructure.persister;

import com.sdc.fgerodez.product.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductPersister {
    long getLastId();

    void saveProduct(Product product);

    void deleteProductById(Long id);

    Optional<Product> findProductById(Long id);

    List<Product> getAllProducts();
}
