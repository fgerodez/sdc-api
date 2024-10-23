package com.sdc.fgerodez.product.infrastructure.serializer;

import com.sdc.fgerodez.product.domain.Product;

import java.util.List;

public interface ProductSerializer {
    String serialize(List<Product> products);

    List<Product> deserialize(String serializedProduct);
}
