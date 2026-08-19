package com.crm.backend.customer;

import java.util.List;

public class CustomerValidationException extends RuntimeException {

	private final List<String> errors;

	public CustomerValidationException(List<String> errors) {
		super("Customer information is not valid.");
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}
}
