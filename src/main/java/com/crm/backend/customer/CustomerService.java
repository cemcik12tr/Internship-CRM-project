package com.crm.backend.customer;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;
import com.crm.backend.customer.dto.CustomerDetailsResponse;
import com.crm.backend.customer.dto.CustomerSearchCriteria;
import com.crm.backend.customer.dto.CustomerSearchResultResponse;
import java.util.List;
import com.crm.backend.customer.dto.UpdateCustomerRequest;

public interface CustomerService {

	CreateCustomerResponse createCustomer(CreateCustomerRequest request);

	List<CustomerSearchResultResponse> searchCustomers(CustomerSearchCriteria criteria);
	List<CustomerSearchResultResponse> getCustomers();
	CustomerDetailsResponse getCustomerDetails(String customerId);
	CustomerDetailsResponse updateCustomer(String customerId,UpdateCustomerRequest request);
	void softDeleteCustomer(String customerId);
}
