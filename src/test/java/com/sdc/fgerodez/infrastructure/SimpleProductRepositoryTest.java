package com.sdc.fgerodez.infrastructure;

import com.sdc.fgerodez.product.domain.InventoryStatus;
import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.persister.ProductPersister;
import com.sdc.fgerodez.product.infrastructure.SimpleProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimpleProductRepositoryTest {

    @Mock
    private ProductPersister persister;

    @InjectMocks
    private SimpleProductRepository sut;

    @Test
    void saveNewProductAssignsIdBasedOnLastPersisterId() {
        when(persister.getLastId()).thenReturn(4L);
        sut = new SimpleProductRepository(persister);

        var savedProduct = sut.save(new Product());

        assertEquals(5, savedProduct.getId());
    }

    @Test
    void saveNewProductSavesWithPersister() {
        var product = new Product();

        sut.save(product);

        verify(persister).saveProduct(notNull());
    }

    @Test
    void saveNewProductAssignsAuditingDates() {
        var product = new Product();

        var savedProduct = sut.save(product);

        assertEquals(savedProduct.getCreatedAt(), savedProduct.getUpdatedAt());
        assertNotNull(savedProduct.getCreatedAt());
    }

    @Test
    void saveExistingProductMaintainsId() {
        var product = createProductWithId();

        var savedProduct = sut.save(product);

        assertEquals(product.getId(), savedProduct.getId());
    }

    @Test
    void saveExistingProductSetsUpdatedDate() {
        var product = createProductWithId();

        var savedProduct = sut.save(product);

        assertTrue(savedProduct.getUpdatedAt() > savedProduct.getCreatedAt());
    }

    @Test
    void findAllReturnsAllProducts() {
        var products = List.of(new Product(), new Product());
        when(persister.getAllProducts()).thenReturn(products);

        var result = sut.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void deleteByIdDeletesWithPersister() {
        sut.deleteById(1L);

        verify(persister).deleteProductById(1L);
    }

    @Test
    void findByIdSearchesWithPersister() {
        sut.findById(1L);

        verify(persister).findProductById(1L);
    }

    @Test
    void findByIdReturnsEmptyWhenStorageIsEmpty() {
        var optionalProduct = sut.findById(1L);

        assertTrue(optionalProduct.isEmpty());
    }

    /*
     * Helpers
     */

    private Product createProductWithId() {
        return new Product(2L, "code", "", "", "", "", new BigDecimal(35), 4, "", 4, 50L, InventoryStatus.LOWSTOCK, 1729929033L, 1729929033L);
    }
}
