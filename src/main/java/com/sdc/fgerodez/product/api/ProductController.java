package com.sdc.fgerodez.product.api;

import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.domain.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Iterable<Product> getAll() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product create(@RequestBody ProductDataDTO productData) {
        var product = ProductMapper.INSTANCE.productDataDtoToProduct(productData);

        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @PatchMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody ProductDataDTO productData) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        ProductMapper.INSTANCE.updateProductFromDTO(productData, product);

        return productRepository.save(product);
    }
}
