package com.crm.backend.customer.dto;

public record CreateCustomerRequest(String nationalId,String gsmNumber,String firstName,String middleName,String lastName) {
}
