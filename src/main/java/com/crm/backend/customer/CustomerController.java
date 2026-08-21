package com.crm.backend.customer;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;
import com.crm.backend.customer.dto.CustomerDetailsResponse;
import com.crm.backend.customer.dto.CustomerSearchCriteria;
import com.crm.backend.customer.dto.CustomerSearchResultResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.crm.backend.customer.dto.UpdateCustomerRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateCustomerResponse createCustomer(@RequestBody CreateCustomerRequest request) {
		return customerService.createCustomer(request);
	}

	@GetMapping("/search")
	public List<CustomerSearchResultResponse> searchCustomers(
			@RequestParam(required = false) String customerId,
			@RequestParam(required = false) String nationalId,
			@RequestParam(required = false) String gsmNumber,
			@RequestParam(required = false) String accountNumber,
			@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String middleName,
			@RequestParam(required = false) String lastName
	) {
		return customerService.searchCustomers(new CustomerSearchCriteria(customerId, nationalId, gsmNumber, accountNumber, firstName, middleName, lastName));
	}

	@GetMapping
	public List<CustomerSearchResultResponse> getCustomers() {
    	return customerService.getCustomers();
	}

	@PutMapping("/{customerId}")
	public CustomerDetailsResponse updateCustomer(@PathVariable String customerId,@RequestBody UpdateCustomerRequest request) {
    	return customerService.updateCustomer(customerId, request);
	}

	@DeleteMapping("/{customerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void softDeleteCustomer(@PathVariable String customerId) {
    	customerService.softDeleteCustomer(customerId);
	}
}
