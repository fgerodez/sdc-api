package com.sdc.fgerodez.product.infrastructure.storage;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.serializer.ProductSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

public class LocalProductStorage implements ProductStorage {

    private final FileChannel fileChannel;

    private final ProductSerializer serializer;

    private final Charset charset;

    public LocalProductStorage(FileChannel fileChannel, ProductSerializer serializer, Charset charset) {
        this.fileChannel = fileChannel;
        this.serializer = serializer;
        this.charset = charset;
    }

    @Override
    public List<Product> readProducts() {
        try {
            if (fileChannel.size() == 0) {
                return List.of();
            }

            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());

            fileChannel.read(buffer);
            fileChannel.position(0);

            buffer.flip();

            var productsData = charset.decode(buffer).toString();

            return serializer.deserialize(productsData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeProducts(List<Product> products) {
        try {
            var lock = fileChannel.lock();
            var buffer = charset.encode(serializer.serialize(products));

            fileChannel.truncate(0);
            fileChannel.write(buffer);
            fileChannel.position(0);

            lock.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
