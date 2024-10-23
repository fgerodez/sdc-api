package com.sdc.fgerodez.infrastructure;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.storage.LocalProductStorage;
import com.sdc.fgerodez.product.infrastructure.serializer.ProductSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalProductStorageTest {

    @Mock
    private FileChannel fileChannel;

    @Mock
    private ProductSerializer serializer;

    @InjectMocks
    private LocalProductStorage sut;

    @BeforeEach
    void setUp() {
        sut = new LocalProductStorage(fileChannel, serializer, StandardCharsets.UTF_8);
    }

    @Test
    void readProductsShouldReturnDeserializedProducts() throws Exception {
        when(fileChannel.size()).thenReturn(15L);

        when(fileChannel.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ((ByteBuffer)invocation.getArgument(0)).put("productsData".getBytes());
            return 10;
        });

        List<Product> expectedProducts = List.of(new Product());
        when(serializer.deserialize("productsData")).thenReturn(expectedProducts);

        List<Product> result = sut.readProducts();

        verify(fileChannel).read(any(ByteBuffer.class));
        verify(fileChannel).position(0);
        assertEquals(expectedProducts, result);
    }

    @Test
    void readProductsShouldThrowRuntimeExceptionOnIOException() throws Exception {
        when(fileChannel.size()).thenReturn(15L);
        when(fileChannel.read(any(ByteBuffer.class))).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> sut.readProducts());
    }

    @Test
    void writeProductsShouldWriteSerializedProducts() throws Exception {
        FileLock mockLock = mock(FileLock.class);
        when(fileChannel.lock()).thenReturn(mockLock);
        when(serializer.serialize(anyList())).thenReturn("serializedProducts");

        sut.writeProducts(List.of());

        verify(fileChannel).lock();
        verify(fileChannel).truncate(0L);
        verify(fileChannel).write(any(ByteBuffer.class));
        verify(fileChannel).position(0);
        verify(mockLock).release();
    }

    @Test
    void writeProductsShouldThrowRuntimeExceptionOnIOException() throws Exception {
        when(fileChannel.lock()).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> sut.writeProducts(List.of()));
    }
}
