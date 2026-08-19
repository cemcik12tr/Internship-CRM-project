package com.crm.backend.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByNationalIdAndStatus(String nationalId, CustomerStatus status);
	boolean existsByGsmNumberAndStatus(String gsmNumber, CustomerStatus status);
}
