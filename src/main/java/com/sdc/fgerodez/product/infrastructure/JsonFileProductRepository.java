package com.sdc.fgerodez.product.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.domain.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class JsonFileProductRepository implements ProductRepository {

    private final ObjectMapper objectMapper;

    private final String dbPath;

    private final AtomicLong lastId;

    public JsonFileProductRepository(ObjectMapper objectMapper, @Value("${json.db.path}") String dbPath) {
        this.objectMapper = objectMapper;
        this.dbPath = dbPath;
        lastId = new AtomicLong(0);
    }

    @PostConstruct
    public void initDb() throws IOException {
        var filePath = Paths.get(dbPath);

        if (Files.notExists(filePath)) {
            Files.writeString(filePath, "[]");
        }

        var lastIdInDb = readAllProducts().stream()
                .map(Product::getId)
                .filter(Objects::nonNull)
                .max(Long::compare)
                .orElse(0L);

        lastId.set(lastIdInDb);
    }

    @Override
    public void deleteById(Long id) {
        var productsList = readAllProducts();

        productsList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .ifPresent(productsList::remove);

        writeAllProducts(productsList);
    }

    @Override
    public Optional<Product> findById(Long id) {
        var productsList = readAllProducts();

        return productsList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        setAuditingFields(product);

        setIdForNewProduct(product);

        var productsList = readAllProducts();

        productsList.stream()
                .filter(p -> p.getId().equals(product.getId()))
                .findFirst()
                .ifPresent(productsList::remove);

        productsList.add(product);

        writeAllProducts(productsList);

        return product;
    }

    @Override
    public Iterable<Product> findAll() {
        return readAllProducts();
    }

    private List<Product> readAllProducts() {
        var filePath = Paths.get(dbPath);

        try {
            var jsonData = Files.readAllBytes(filePath);

            return objectMapper.readValue(jsonData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeAllProducts(List<Product> products) {
        var filePath = Paths.get(dbPath);

        try {
            var jsonData = objectMapper.writeValueAsBytes(products);

            Files.write(filePath, jsonData, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
