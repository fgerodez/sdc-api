package com.sdc.fgerodez.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.serializer.JacksonProductSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JacksonProductSerializerTest {

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    JacksonProductSerializer sut;

    @Test
    void serializeUsesJacksonToReturnJsonValue() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenReturn("json");

        var result = sut.serialize(List.of(new Product()));

        assertEquals("json", result);
    }

    @Test
    void serializeThrowsRuntimeExceptionWhenSerializationFails() throws Exception {
        when(objectMapper.writeValueAsString(anyList())).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> sut.serialize(List.of(new Product())));
    }

    @Test
    void deserializeUsesJacksonToReturnProductList() throws Exception {
        var json = "json";
        var product = new Product();
        when(objectMapper.readValue(eq(json), any(TypeReference.class))).thenReturn(List.of(product));

        var result = sut.deserialize(json);

        assertTrue(result.contains(product));
    }

    @Test
    void deserializeThrowsRuntimeExceptionWhenDeserializationFails() throws Exception {
        when(objectMapper.readValue(any(String.class), any(TypeReference.class))).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> sut.deserialize(""));
    }
}
