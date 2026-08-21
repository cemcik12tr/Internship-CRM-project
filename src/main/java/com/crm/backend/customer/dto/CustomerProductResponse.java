package com.crm.backend.customer.dto;

public record CustomerProductResponse(
		Long productId,
		String productName,
		Double price,
		String status
) {
}
