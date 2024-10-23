package com.sdc.fgerodez.product.infrastructure;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.domain.ProductRepository;
import com.sdc.fgerodez.product.infrastructure.storage.ProductStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileProductRepository implements ProductRepository {

    private final ProductStorage storage;

    private final AtomicLong lastId;

    public FileProductRepository(ProductStorage storage) {
        this.storage = storage;

        lastId = new AtomicLong(findMaxId());
    }

    @Override
    public void deleteById(Long id) {
        var products = readProductsMap();

        products.remove(id);

        storage.writeProducts(products.values());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(readProductsMap().get(id));
    }

    @Override
    public Product save(Product product) {
        setAuditingFields(product);

        setIdForNewProduct(product);

        var products = readProductsMap();

        products.put(product.getId(), product);

        storage.writeProducts(products.values());

        return product;
    }

    @Override
    public List<Product> findAll() {
        return storage.readProducts().stream().toList();
    }

    private void setAuditingFields(Product product) {
        var now = System.currentTimeMillis();

        if (product.getId() == null) {
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
        } else {
            product.setUpdatedAt(now);
        }
    }

    private void setIdForNewProduct(Product product) {
        if (product.getId() == null) {
            product.setId(lastId.incrementAndGet());
        }
    }

    private long findMaxId() {
        return storage.readProducts()
                .stream()
                .map(Product::getId)
                .max(Long::compare)
                .orElse(0L);
    }

    private Map<Long, Product> readProductsMap() {
        return storage.readProducts()
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }
}
