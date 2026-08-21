package com.crm.backend.customer;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByCustomerId(String customerId);
	boolean existsByAccountNumber(String accountNumber);
	boolean existsByNationalIdAndStatus(String nationalId, CustomerStatus status);
	boolean existsByGsmNumberAndStatus(String gsmNumber, CustomerStatus status);

	@Query("""
			select customer from Customer customer
			where customer.status <> :deletedStatus
			and (:customerId is null or customer.customerId = :customerId)
			and (:nationalId is null or customer.nationalId = :nationalId)
			and (:gsmNumber is null or customer.gsmNumber = :gsmNumber)
			and (:accountNumber is null or customer.accountNumber = :accountNumber)
			and lower(customer.firstName) like concat('%', lower(coalesce(:firstName, '')), '%')
			and lower(coalesce(customer.middleName, '')) like concat('%', lower(coalesce(:middleName, '')), '%')
			and lower(customer.lastName) like concat('%', lower(coalesce(:lastName, '')), '%')
			order by customer.createdDate desc
			""")
	List<Customer> searchCustomers(
			@Param("customerId") String customerId,
			@Param("nationalId") String nationalId,
			@Param("gsmNumber") String gsmNumber,
			@Param("accountNumber") String accountNumber,
			@Param("firstName") String firstName,
			@Param("middleName") String middleName,
			@Param("lastName") String lastName,
			@Param("deletedStatus") CustomerStatus deletedStatus
	);
	List<Customer> findAllByStatusNotOrderByCreatedDateDesc(CustomerStatus status);
	java.util.Optional<Customer> findByCustomerIdAndStatusNot(String customerId, CustomerStatus status);

	@Query("""
        select count(customer) > 0
        from Customer customer
        where customer.gsmNumber = :gsmNumber
        and customer.customerId <> :customerId
        and customer.status in :statuses
        """)
	boolean existsDuplicateGsm(@Param("gsmNumber") String gsmNumber,@Param("customerId") String customerId,@Param("statuses") List<CustomerStatus> statuses);
}
