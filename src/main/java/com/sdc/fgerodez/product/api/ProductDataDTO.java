package com.sdc.fgerodez.product.api;

import com.sdc.fgerodez.product.domain.InventoryStatus;

import java.math.BigDecimal;

public record ProductDataDTO(String code, String name, String description, String image, String category,
                             BigDecimal price, int quantity, String internalReference, Integer rating, Long shellId,
                             InventoryStatus inventoryStatus) {
}
