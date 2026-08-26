package com.crm.backend.customer.dto;

public record CustomerSearchResultResponse(
		String customerId,
		String customerName,
		String accountNumber,
		String gsmNumber,
		String status
) {
}
