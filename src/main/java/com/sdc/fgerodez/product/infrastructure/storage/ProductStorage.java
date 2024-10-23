package com.sdc.fgerodez.product.infrastructure.storage;

import com.sdc.fgerodez.product.domain.Product;

import java.util.List;

public interface ProductStorage {
    List<Product> readProducts();

    void writeProducts(List<Product> products);
}
