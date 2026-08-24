package com.crm.backend.model;

import com.crm.backend.model.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Table(name = "Catalogs")
@Data

public class Catalog {

    @Id
    @Column(name ="catalog_id", length = 100, nullable = false, updatable = false)
    private String id;

    @Column(name = "catalog_name", length= 100, unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

     @Column(name = "created_by", updatable = false)
    private String createdBy;


    @Column(name = "updated_date", nullable = true, insertable = false)
    private LocalDateTime updatedDate;

    @Column(name = "updated_by",nullable = true, insertable = false)
    private String updatedBy;

    @Column(name = "deleted_date") 
    private LocalDateTime deletedDate;

    @Column(name = "deleted_by")
    private String deletedBy;


}