package com.crm.backend.customer;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerExceptionHandler {

	@ExceptionHandler(CustomerValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleCustomerValidationException(CustomerValidationException exception) {
		return new ErrorResponse("VALIDATION_ERROR", exception.getErrors());
	}

	@ExceptionHandler(DuplicateCustomerException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDuplicateCustomerException(DuplicateCustomerException exception) {
		return new ErrorResponse("DUPLICATE_CUSTOMER", List.of(exception.getMessage()));
	}

	public record ErrorResponse(String code, List<String> messages) {
	}
}
