package com.sdc.fgerodez.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.infrastructure.serializer.JacksonProductSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
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

    final Charset charset = StandardCharsets.UTF_8;

    @BeforeEach
    void setUp() {
        sut = new JacksonProductSerializer(objectMapper, charset);
    }

    @Test
    void serializeUsesJacksonAndCharsetToEncodeValue() throws Exception {
        when(objectMapper.writeValueAsString(anyCollection())).thenReturn("json");

        var result = sut.serialize(Collections.emptyList());

        assertArrayEquals(charset.encode("json").array(), result);
    }

    @Test
    void serializeThrowsRuntimeExceptionWhenSerializationFails() throws Exception {
        when(objectMapper.writeValueAsString(anyCollection())).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> sut.serialize(Collections.emptyList()));
    }

    @Test
    void deserializeUsesJacksonAndCharsetToDecodeValue() throws Exception {
        var expectedResult = List.of();
        var encodedValue = charset.encode("json").array();

        when(objectMapper.readValue(eq("json"), any(TypeReference.class))).thenReturn(expectedResult);

        var result = sut.deserialize(encodedValue);

        assertSame(expectedResult, result);
    }

    @Test
    void deserializeThrowsRuntimeExceptionWhenDeserializationFails() throws Exception {
        when(objectMapper.readValue(any(String.class), any(TypeReference.class))).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> sut.deserialize(new byte[0]));
    }
}
