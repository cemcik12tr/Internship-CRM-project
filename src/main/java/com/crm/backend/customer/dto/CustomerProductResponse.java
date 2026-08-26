package com.crm.backend.customer.dto;

public record CustomerProductResponse(
		String productId,
		String productName,
		java.math.BigDecimal price,
		String status
) {
}
