package com.crm.backend.repository;

import com.crm.backend.model.Product;
import com.crm.backend.model.enums.Status;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    boolean existsByNameIgnoreCaseAndCatalogId(String name, String catalogId);

    boolean existsByNameIgnoreCaseAndCatalogIdAndIdNot(String name, String catalogId, String id);
    
    @Query("SELECT p FROM Product p WHERE p.status != 'DELETED'" +
        "AND (:id IS NULL OR p.id = :id) " +
        "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',CAST(:name AS String) ,'%')))"+
        "AND (:catalogName IS NULL OR LOWER(p.catalog.name) LIKE LOWER(CONCAT('%', CAST(:catalogName AS String), '%')))" +
        "AND ( (:status IS NOT NULL AND p.status = :status) OR (:status IS NULL AND p.status = 'ACTIVE') ) " +
        "AND (p.status = 'INACTIVE' OR p.catalog.status = 'ACTIVE') " +
        "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
        "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +   
        "AND (:stockStatus IS NULL OR " +
        "    (:stockStatus = 'In Stock' AND    p.stock > 0) OR " +
        "    (:stockStatus = 'Out Of Stock' AND p.stock = 0)) ")
    List<Product> searchProducts(
        @Param("id") String id,
        @Param("name") String name,
        @Param("catalogName") String catalogName,
        @Param("stockStatus") String stockStatus,
        @Param("status") Status status,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice
    );

}