package com.crm.backend.repository;

import com.crm.backend.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCustomerIdAndIsActiveTrue(String customerId);
}
