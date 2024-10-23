package com.sdc.fgerodez.product.infrastructure.serializer;

import com.sdc.fgerodez.product.domain.Product;

import java.util.Collection;

public interface ProductSerializer {
    byte[] serialize(Collection<Product> products);

    Collection<Product> deserialize(byte[] serializedProduct);
}
