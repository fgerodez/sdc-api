package com.sdc.fgerodez.infrastructure;

import com.sdc.fgerodez.product.domain.InventoryStatus;
import com.sdc.fgerodez.product.domain.Product;
import com.sdc.fgerodez.product.infrastructure.FileProductRepository;
import com.sdc.fgerodez.product.infrastructure.storage.ProductStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileProductRepositoryTest {

    @Mock
    private ProductStorage storage;

    @Captor
    private ArgumentCaptor<Collection<Product>> productsCaptor;

    @InjectMocks
    private FileProductRepository sut;

    @Test
    void saveNewProductUsesNextAvailableId() {
        var existingId = populateStorageWithOneProduct();

        sut = new FileProductRepository(storage);

        var savedProduct = sut.save(new Product());

        assertProductWasAssignedId(existingId + 1, savedProduct);
    }

    @Test
    void saveProductSavesToStorage() {
        var product = new Product();

        sut.save(product);

        assertProductWasSavedToStorage(product);
    }

    @Test
    void saveExistingProductOverridesExistingData() {
        populateStorageWithProductIdAndCode(4, "code");

        var updatedProduct = createProductWithIdAndCode(4, "updatedCode");

        sut.save(updatedProduct);

        assertProductWasUpdatedInStorage(updatedProduct);
    }

    @Test
    void saveNewProductAssignsAuditingDates() {
        var product = new Product();

        var savedProduct = sut.save(product);

        assertAuditingDatesHaveBeenSet(savedProduct);
    }

    @Test
    void saveExistingProductMaintainsId() {
        var product = createProductWithId(4);

        var savedProduct = sut.save(product);

        assertProductIdsAreTheSame(product, savedProduct);
    }

    @Test
    void saveExistingProductSetsUpdatedDate() {
        var product = createProductWithId(4);

        var savedProduct = sut.save(product);

        assertUpdatedAtIsGreaterThanCreatedAt(savedProduct);
    }

    @Test
    void findAllReturnsAllProducts() {
        populateStorageWithTwoProducts();

        var result = sut.findAll();

        assertListContainsOnlyStorageProducts(result);
    }

    @Test
    void deleteByIdDeletesWithPersister() {
        populateStorageWithTwoProductIds(1, 2);

        sut.deleteById(1L);

        assertStorageContainsOnlyProductWithId(2);
    }

    @Test
    void findByIdReturnsProductWithCorrectId() {
        populateStorageWithTwoProductIds(1, 2);

        var optionalProduct = sut.findById(1L);

        assertProductWasFoundWithId(1, optionalProduct);
    }

    @Test
    void findByIdReturnsEmptyWhenStorageIsEmpty() {
        var optionalProduct = sut.findById(1L);

        assertProductWasNotFound(optionalProduct);
    }

    /*
     * Assertions
     */

    private void assertProductWasAssignedId(int id, Product product) {
        assertEquals(id, product.getId());
    }

    private void assertProductWasUpdatedInStorage(Product product) {
        verify(storage).writeProducts(productsCaptor.capture());

        assertTrue(productsCaptor.getValue().contains(product));
        assertEquals("updatedCode", product.getCode());
    }

    private void assertListContainsOnlyStorageProducts(List<Product> result) {
        assertEquals(2, result.size());
    }

    private void assertAuditingDatesHaveBeenSet(Product product) {
        assertNotNull(product.getCreatedAt());
        assertEquals(product.getUpdatedAt(), product.getCreatedAt());
    }

    private void assertProductIdsAreTheSame(Product fst, Product snd) {
        assertEquals(fst.getId(), snd.getId());
    }

    private void assertProductWasSavedToStorage(Product product) {
        verify(storage).writeProducts(productsCaptor.capture());
        assertTrue(productsCaptor.getValue().contains(product));
    }

    private void assertUpdatedAtIsGreaterThanCreatedAt(Product product) {
        assertTrue(product.getUpdatedAt() > product.getCreatedAt());
    }

    private void assertStorageContainsOnlyProductWithId(int id) {
        verify(storage).writeProducts(productsCaptor.capture());

        assertEquals(id, productsCaptor.getValue().iterator().next().getId());
        assertEquals(1, productsCaptor.getValue().size());
    }

    private void assertProductWasFoundWithId(int id, Optional<Product> optionalProduct) {
        assertTrue(optionalProduct.isPresent());
        assertEquals(id, optionalProduct.get().getId());
    }

    private void assertProductWasNotFound(Optional<Product> optionalProduct) {
        assertTrue(optionalProduct.isEmpty());
    }

    /*
     * Mock setup
     */

    private Product createProductWithId(int id) {
        return new Product((long) id, "code", "", "", "", "", new BigDecimal(35), 4, "", 4, 50L, InventoryStatus.LOWSTOCK, 1729929033L, 1729929033L);
    }

    private int populateStorageWithOneProduct() {
        var id = 5;
        when(storage.readProducts()).thenReturn(List.of(createProductWithId(id)));

        return id;
    }

    private void populateStorageWithTwoProducts() {
        populateStorageWithTwoProductIds(1, 2);
    }

    private void populateStorageWithTwoProductIds(int fstId, int sndId) {
        when(storage.readProducts()).thenReturn(List.of(createProductWithId(fstId), createProductWithId(sndId)));
    }

    private Product createProductWithIdAndCode(int id, String code) {
        var product = createProductWithId(id);
        product.setCode(code);

        return product;
    }

    private void populateStorageWithProductIdAndCode(int id, String code) {
        var product = createProductWithIdAndCode(id, code);
        when(storage.readProducts()).thenReturn(List.of(product));
    }
}
