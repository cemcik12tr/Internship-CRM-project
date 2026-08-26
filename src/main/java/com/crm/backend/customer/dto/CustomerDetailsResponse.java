package com.crm.backend.customer.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerDetailsResponse(
		String customerId,
		String firstName,
		String middleName,
		String lastName,
		String nationalId,
		String gsmNumber,
		String accountNumber,
		String status,
		LocalDateTime createdDate,
		String createdBy,
		LocalDateTime updatedDate,
		String updatedBy,
		List<CustomerProductResponse> products
) {
}
