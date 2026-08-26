package com.crm.backend.customer.dto;

import com.crm.backend.customer.CustomerStatus;

public record UpdateCustomerRequest(String gsmNumber,String firstName,String middleName, String lastName,CustomerStatus status) {
}