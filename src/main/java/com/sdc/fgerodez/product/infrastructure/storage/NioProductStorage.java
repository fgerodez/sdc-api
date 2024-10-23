package com.sdc.fgerodez.product.infrastructure.storage;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.serializer.ProductSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Collections;

public class NioProductStorage implements ProductStorage {

    private final FileChannel fileChannel;

    private final ProductSerializer serializer;

    public NioProductStorage(FileChannel fileChannel, ProductSerializer serializer) {
        this.fileChannel = fileChannel;
        this.serializer = serializer;
    }

    @Override
    public Collection<Product> readProducts() {
        try {
            if (fileChannel.size() == 0) {
                return Collections.emptyList();
            }

            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());

            fileChannel.read(buffer);
            fileChannel.position(0);

            buffer.flip();

            return serializer.deserialize(buffer.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeProducts(Collection<Product> products) {
        try {
            var lock = fileChannel.lock();
            var buffer = ByteBuffer.wrap(serializer.serialize(products));

            fileChannel.truncate(0);
            fileChannel.write(buffer);
            fileChannel.position(0);

            lock.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
