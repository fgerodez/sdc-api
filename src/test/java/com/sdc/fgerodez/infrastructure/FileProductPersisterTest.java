package com.sdc.fgerodez.infrastructure;

import com.sdc.fgerodez.product.domain.InventoryStatus;
import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.persister.FileProductPersister;
import com.sdc.fgerodez.product.infrastructure.storage.ProductStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileProductPersisterTest {

    @Mock
    private ProductStorage storage;

    @Captor
    private ArgumentCaptor<List<Product>> productListCaptor;

    @InjectMocks
    private FileProductPersister sut;

    @Test
    void saveProductWritesProductToStorage() {
        Product product = new Product();

        sut.saveProduct(product);

        verify(storage).writeProducts(productListCaptor.capture());
        List<Product> capturedList = productListCaptor.getValue();

        assertEquals(capturedList.size(), 1);
        assertTrue(capturedList.contains(product));
    }

    @Test
    void saveProductOverridesExistingProductWithSameId() {
        var existingProduct = createProduct(1L, "code");
        var updatedProduct = createProduct(1L, "updatedCode");
        when(storage.readProducts()).thenReturn(List.of(existingProduct));

        sut.saveProduct(updatedProduct);

        verify(storage).writeProducts(productListCaptor.capture());
        List<Product> productList = productListCaptor.getValue();

        assertEquals(1, productList.size());
        assertEquals("updatedCode", productList.getFirst().getCode());
    }

    @Test
    void getAllProductsReadsFromStorage() {
        List<Product> productList = List.of();
        when(storage.readProducts()).thenReturn(productList);

        var result = sut.getAllProducts();

        assertSame(result, productList);
    }

    @Test
    void getLastIdReturnsMaxProductId() {
        when(storage.readProducts()).thenReturn(createProductList(20L, 4L));

        var result = sut.getLastId();

        assertEquals(20L, result);
    }

    @Test
    void deleteProductByIdRemovesTheRightProduct() {
        when(storage.readProducts()).thenReturn(createProductList(15L, 4L));

        sut.deleteProductById(15L);

        verify(storage).writeProducts(productListCaptor.capture());
        List<Product> productList = productListCaptor.getValue();

        assertEquals(1, productList.size());
        assertEquals(4L, productList.getFirst().getId());
    }

    @Test
    void findProductByIdReturnsTheRightProduct() {
        when(storage.readProducts()).thenReturn(createProductList(10L, 6L));

        var result = sut.findProductById(6L);

        assertTrue(result.isPresent());
        assertEquals(6L, result.get().getId());
    }

    @Test
    void findProductByIdReturnsEmptyIfProductDoesNotExist() {
        when(storage.readProducts()).thenReturn(List.of());

        var result = sut.findProductById(4L);

        assertTrue(result.isEmpty());
    }

    /*
     * Helpers
     */

    private List<Product> createProductList(Long firstId, Long secondId) {
        var firstProduct = createProduct(firstId, "code");
        var secondProduct = createProduct(secondId, "code2");

        return List.of(firstProduct, secondProduct);
    }

    private Product createProduct(Long id, String code) {
        return new Product(id, code, "", "", "", "", new BigDecimal(35), 4, "", 4, 50L, InventoryStatus.LOWSTOCK, 1729929033L, 1729929033L);
    }
}
