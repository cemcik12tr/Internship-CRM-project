package com.crm.backend.customer;

public class CustomerNotFoundException extends RuntimeException {

	public CustomerNotFoundException(String customerId) {
		super("Customer not found: " + customerId);
	}
}
