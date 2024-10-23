package com.sdc.fgerodez.product.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String name;

    private String description;

    private String image;

    private String category;

    private BigDecimal price;

    private int quantity;

    private String internalReference;

    private Long shellId;

    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;

    private Integer rating;

    @CreatedDate
    private Long createdAt;

    @LastModifiedDate
    private Long updatedAt;

    public Product() {

    }

    public Product(Long id, String code, String name, String description, String image, String category, BigDecimal price, int quantity, String internalReference, Integer rating, Long shellId, InventoryStatus inventoryStatus, Long createdAt, Long updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.internalReference = internalReference;
        this.rating = rating;
        this.shellId = shellId;
        this.inventoryStatus = inventoryStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getInternalReference() {
        return internalReference;
    }

    public Long getShellId() {
        return shellId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public InventoryStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public Integer getRating() {
        return rating;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setInternalReference(String internalReference) {
        this.internalReference = internalReference;
    }

    public void setShellId(Long shellId) {
        this.shellId = shellId;
    }

    public void setInventoryStatus(InventoryStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
