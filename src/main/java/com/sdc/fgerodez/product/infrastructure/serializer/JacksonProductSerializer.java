package com.sdc.fgerodez.product.infrastructure.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.domain.Product;

import java.util.List;

public class JacksonProductSerializer implements ProductSerializer {

    private final ObjectMapper mapper;

    public JacksonProductSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(List<Product> products) {
        try {
            return mapper.writeValueAsString(products);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> deserialize(String serializedProduct) {
        try {
            return mapper.readValue(serializedProduct, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
