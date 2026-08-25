package com.crm.backend.model;

import com.crm.backend.model.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Products")
@Data

public class Product
 {
    @Id 
    @Column(name = "product_id",length=20, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id",nullable = false)
    private Catalog catalog;

    @Column(name = "product_name", length = 100 , nullable = false)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2 )
    private BigDecimal price;

    @Column(name = "stock_status", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_date", updatable = false) 
    private LocalDateTime createdDate;
   
    @Column(name = "created_by", updatable = false)
    private String createdBy;

   
    @Column(name = "updated_date" , nullable = true, insertable = false) 
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
   
    @Column(name = "deleted_date") 
    private LocalDateTime deletedDate;

    @Column(name = "deleted_by")
    private String deletedBy;

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

}
