package com.crm.backend.customer;

public class DuplicateCustomerException extends RuntimeException {

	public DuplicateCustomerException(String message) {
		super(message);
	}
}
