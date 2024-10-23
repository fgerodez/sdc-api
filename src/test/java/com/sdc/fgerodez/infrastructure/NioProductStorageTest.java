package com.sdc.fgerodez.infrastructure;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.serializer.ProductSerializer;
import com.sdc.fgerodez.product.infrastructure.storage.NioProductStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NioProductStorageTest {

    @Mock
    private FileChannel fileChannel;

    @Mock
    private ProductSerializer serializer;

    @InjectMocks
    private NioProductStorage sut;

    @Test
    void readProductsShouldReturnDeserializedProducts() throws Exception {
        when(fileChannel.size()).thenReturn(15L);

        when(fileChannel.read(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ((ByteBuffer) invocation.getArgument(0)).put("productsData".getBytes());
            return 10;
        });

        Set<Product> expectedProducts = Set.of(new Product());
        when(serializer.deserialize(any())).thenReturn(expectedProducts);

        Collection<Product> result = sut.readProducts();

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
        when(serializer.serialize(anySet())).thenReturn(new byte[0]);

        sut.writeProducts(Set.of());

        verify(fileChannel).lock();
        verify(fileChannel).truncate(0L);
        verify(fileChannel).write(any(ByteBuffer.class));
        verify(fileChannel).position(0);
        verify(mockLock).release();
    }

    @Test
    void writeProductsShouldThrowRuntimeExceptionOnIOException() throws Exception {
        when(fileChannel.lock()).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> sut.writeProducts(Set.of()));
    }
}
