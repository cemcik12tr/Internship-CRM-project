package com.crm.backend.customer;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;

public interface CustomerService {

	CreateCustomerResponse createCustomer(CreateCustomerRequest request);
}
