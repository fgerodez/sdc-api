package com.sdc.fgerodez.product.infrastructure.storage;

import com.sdc.fgerodez.product.domain.Product;

import java.util.Collection;

public interface ProductStorage {
    Collection<Product> readProducts();

    void writeProducts(Collection<Product> products);
}
