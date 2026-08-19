package com.crm.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "Products")
@Data
@SQLDelete(sql= " UPDATE products SET is_active = false WHERE id=?")
@SQLRestriction("is_active=true")

public class Product
 {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(name = "catalog_id") 
    private Long catalogId;

    private String name;
    private Double price;
    private Integer stock;

    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_date") private LocalDateTime createdDate;
    @Column(name = "updated_date") private LocalDateTime updatedDate;
    @Column(name = "deleted_date") private LocalDateTime deletedDate;



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCatalogId() {
        return catalogId;
    }
    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }
    public void setDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }



}
