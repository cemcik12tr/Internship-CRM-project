package com.crm.backend.customer.dto;

public record CreateCustomerResponse(
		String customerId,
		String accountNumber,
		String status
) {
}
