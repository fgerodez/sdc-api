package com.sdc.fgerodez.product.infrastructure;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.domain.ProductRepository;
import com.sdc.fgerodez.product.infrastructure.persister.ProductPersister;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleProductRepository implements ProductRepository {

    private final ProductPersister fileSystem;

    private final AtomicLong lastId;

    public SimpleProductRepository(ProductPersister persister) {
        this.fileSystem = persister;

        lastId = new AtomicLong(persister.getLastId());
    }

    @Override
    public void deleteById(Long id) {
        fileSystem.deleteProductById(id);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return fileSystem.findProductById(id);
    }

    @Override
    public Product save(Product product) {
        setAuditingFields(product);

        setIdForNewProduct(product);

        fileSystem.saveProduct(product);

        return product;
    }

    @Override
    public List<Product> findAll() {
        return fileSystem.getAllProducts();
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
}
