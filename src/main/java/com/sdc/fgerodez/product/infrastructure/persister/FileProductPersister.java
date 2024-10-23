package com.sdc.fgerodez.product.infrastructure.persister;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.storage.ProductStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileProductPersister implements ProductPersister {

    private final ProductStorage storage;

    public FileProductPersister(ProductStorage storage) {
        this.storage = storage;
    }

    @Override
    public void saveProduct(Product product) {
        var productsList = new ArrayList<>(getAllProducts());

        productsList.removeIf(p -> p.getId().equals(product.getId()));

        productsList.add(product);

        storage.writeProducts(productsList);
    }

    @Override
    public List<Product> getAllProducts() {
        return storage.readProducts();
    }

    @Override
    public long getLastId() {
        return getAllProducts().stream()
                .map(Product::getId)
                .max(Long::compare)
                .orElse(0L);
    }

    @Override
    public void deleteProductById(Long id) {
        var productsList = new ArrayList<>(getAllProducts());

        productsList.removeIf(p -> p.getId().equals(id));

        storage.writeProducts(productsList);
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return getAllProducts()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
}
