package com.sdc.fgerodez.product.infrastructure.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.domain.Product;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;

public class JacksonProductSerializer implements ProductSerializer {

    private final ObjectMapper mapper;

    private final Charset charset;

    public JacksonProductSerializer(ObjectMapper mapper, Charset charset) {
        this.mapper = mapper;
        this.charset = charset;
    }

    @Override
    public byte[] serialize(Collection<Product> products) {
        try {
            var buffer = charset.encode(mapper.writeValueAsString(products));
            var byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);

            return byteArray;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Product> deserialize(byte[] serializedProduct) {
        try {
            var json = charset.decode(ByteBuffer.wrap(serializedProduct)).toString();

            return mapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
