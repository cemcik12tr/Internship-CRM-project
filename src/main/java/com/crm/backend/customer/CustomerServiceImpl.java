package com.crm.backend.customer;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;
import com.crm.backend.customer.dto.CustomerDetailsResponse;
import com.crm.backend.customer.dto.CustomerProductResponse;
import com.crm.backend.customer.dto.CustomerSearchCriteria;
import com.crm.backend.customer.dto.CustomerSearchResultResponse;
import com.crm.backend.model.Product;
import com.crm.backend.model.enums.Status;
import com.crm.backend.repository.ProductRepository;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.crm.backend.customer.dto.UpdateCustomerRequest;
import java.time.LocalDateTime;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String NAME_PATTERN = "^[A-Za-zÇĞİÖŞÜçğıöşü ]{2,50}$";
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository, ProductRepository productRepository) {
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
	}

	@Override
	@Transactional
	public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
		validate(request);
		checkDuplicates(request);

		Customer customer = new Customer();
		customer.setCustomerId(generateCustomerId());
		customer.setAccountNumber(generateAccountNumber());
		customer.setNationalId(request.nationalId().trim());
		customer.setGsmNumber(request.gsmNumber().trim());
		customer.setFirstName(request.firstName().trim());
		customer.setMiddleName(isBlank(request.middleName()) ? null : request.middleName().trim());
		customer.setLastName(request.lastName().trim());
		customer.setStatus(CustomerStatus.ACTIVE);
		customer.setCreatedBy(currentActor());

		Customer savedCustomer = customerRepository.save(customer);
		return new CreateCustomerResponse(savedCustomer.getCustomerId(),savedCustomer.getAccountNumber(),savedCustomer.getStatus().name());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerSearchResultResponse> searchCustomers(CustomerSearchCriteria criteria) {
		CustomerSearchCriteria normalizedCriteria = criteria.normalized();
		return customerRepository.searchCustomers(
				normalizedCriteria.customerId(),
				normalizedCriteria.nationalId(),
				normalizedCriteria.gsmNumber(),
				normalizedCriteria.accountNumber(),
				normalizedCriteria.firstName(),
				normalizedCriteria.middleName(),
				normalizedCriteria.lastName(),
				CustomerStatus.ACTIVE
		).stream().map(this::toSearchResult).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerDetailsResponse getCustomerDetails(String customerId) {
		Customer customer = customerRepository.findByCustomerIdAndStatusNot(customerId, CustomerStatus.DELETED).orElseThrow(() -> new CustomerNotFoundException(customerId));

		List<CustomerProductResponse> products = productRepository.findByCustomerIdAndStatus(customer.getCustomerId(), Status.ACTIVE)
				.stream().map(this::toProductResponse).toList();

		return new CustomerDetailsResponse(
				customer.getCustomerId(),
				customer.getFirstName(),
				customer.getMiddleName(),
				customer.getLastName(),
				customer.getNationalId(),
				customer.getGsmNumber(),
				customer.getAccountNumber(),
				customer.getStatus().name(),
				customer.getCreatedDate(),
				customer.getCreatedBy(),
				customer.getUpdatedDate(),
				customer.getUpdatedBy(),
				products
		);
	}

	private void validate(CreateCustomerRequest request) {
		List<String> errors = new ArrayList<>();

		if (isBlank(request.nationalId()) || !request.nationalId().matches("^\\d{11}$")) {
			errors.add("National ID must contain exactly 11 numeric characters.");
		}
		if (isBlank(request.gsmNumber()) || !request.gsmNumber().matches("^5\\d{9}$")) {
			errors.add("GSM number must be in a valid format: 5XXXXXXXXX.");
		}
		validateName(request.firstName(), "First name", true, errors);
		validateName(request.middleName(), "Middle name", false, errors);
		validateName(request.lastName(), "Last name", true, errors);

		if (!errors.isEmpty()) {
			throw new CustomerValidationException(errors);
		}
	}

	private void checkDuplicates(CreateCustomerRequest request) {

    List<CustomerStatus> duplicateStatuses =
            List.of(CustomerStatus.ACTIVE, CustomerStatus.DELETED);

    	if (customerRepository.existsByNationalIdAndStatusIn(request.nationalId().trim(),duplicateStatuses)) {

        	throw new DuplicateCustomerException( "National ID already belongs to another customer.");
   	 }
    	if (customerRepository.existsByGsmNumberAndStatusIn(request.gsmNumber().trim(), duplicateStatuses)) {
        	throw new DuplicateCustomerException("GSM number already belongs to another customer.");
    	}
	}
	private void validateUpdate(UpdateCustomerRequest request) {
    	List<String> errors = new ArrayList<>();
    	if (isBlank(request.gsmNumber()) || !request.gsmNumber().matches("^5\\d{9}$")) {
        		errors.add("GSM number must be in a valid format: 5XXXXXXXXX.");
    	}

    	validateName(request.firstName(), "First name", true, errors);
    	validateName(request.middleName(), "Middle name", false, errors);
    	validateName(request.lastName(), "Last name", true, errors);

    	if (request.status() == null) {
        	errors.add("Status is mandatory.");
    	} else if (request.status() == CustomerStatus.DELETED) {
        	errors.add("Customer status cannot be changed to DELETED by update.");
    	}

    	if (!errors.isEmpty()) {
       	 throw new CustomerValidationException(errors);
    	}
	}

	private void checkGsmDuplicate(String customerId, String gsmNumber) {
    	if (customerRepository.existsDuplicateGsm(gsmNumber.trim(),customerId,List.of(CustomerStatus.ACTIVE, CustomerStatus.DELETED))) {
        	throw new DuplicateCustomerException("GSM number already belongs to another customer.");
    	}
	}

	private String currentActor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null
				|| !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken
				|| "anonymousUser".equals(authentication.getName())) {
			return "system";
		}

		return authentication.getName();
	}

	private void validateName(String value, String fieldName, boolean required, List<String> errors) {
		if (isBlank(value)) {
			if (required) {
				errors.add(fieldName + " is mandatory.");
			}
			return;
		}
		if (!value.trim().matches(NAME_PATTERN)) {
			errors.add(fieldName + " must contain between 2 and 50 alphabetical characters.");
		}
	}

	private String generateCustomerId() {
		String customerId;
		do {
			customerId = "CUST" + randomDigits(10);
		} while (customerRepository.existsByCustomerId(customerId));
		return customerId;
	}

	private String generateAccountNumber() {
		String accountNumber;
		do {
			accountNumber = "ACC" + randomDigits(10);
		} while (customerRepository.existsByAccountNumber(accountNumber));
		return accountNumber;
	}

	private String randomDigits(int length) {
		StringBuilder value = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			value.append(RANDOM.nextInt(10));
		}
		return value.toString();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private CustomerSearchResultResponse toSearchResult(Customer customer) {
		return new CustomerSearchResultResponse(
				customer.getCustomerId(),
				fullName(customer),
				customer.getAccountNumber(),
				customer.getGsmNumber(),
				customer.getStatus().name());
	}

	private CustomerProductResponse toProductResponse(Product product) {
		return new CustomerProductResponse(
			product.getId(),
			product.getName(), 
			product.getPrice(),
			product.getStatus() != null ? product.getStatus().name() : "PASSIVE"
		);
	}

	private String fullName(Customer customer) {
		return Stream.of(
				customer.getFirstName(),
				customer.getMiddleName(),
				customer.getLastName()).filter(value -> value != null && !value.isBlank()).collect(java.util.stream.Collectors.joining(" "));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerSearchResultResponse> getCustomers() {
    	return customerRepository
            	.findAllByStatusOrderByCreatedDateDesc(CustomerStatus.ACTIVE)
            	.stream()
            	.map(this::toSearchResult)
            	.toList();
	}

	@Override
	@Transactional
	public CustomerDetailsResponse updateCustomer(String customerId,UpdateCustomerRequest request) {
		Customer customer = customerRepository.findByCustomerIdAndStatusNot(customerId, CustomerStatus.DELETED).orElseThrow(() -> new CustomerNotFoundException(customerId));
		validateUpdate(request);
		checkGsmDuplicate(customerId, request.gsmNumber());
    	customer.setGsmNumber(request.gsmNumber().trim());
    	customer.setFirstName(request.firstName().trim());
		customer.setMiddleName(isBlank(request.middleName()) ? null : request.middleName().trim());
		customer.setLastName(request.lastName().trim());
		customer.setStatus(request.status());
		customer.setUpdatedDate(LocalDateTime.now());
		customer.setUpdatedBy(currentActor());
		customerRepository.save(customer);

    	return getCustomerDetails(customerId);
	}
	@Override
	@Transactional
	public void softDeleteCustomer(String customerId) {
		Customer customer = customerRepository.findByCustomerIdAndStatusNot(customerId, CustomerStatus.DELETED)
				.orElseThrow(() -> new CustomerNotFoundException(customerId));

		if (customer.getStatus() != CustomerStatus.ACTIVE) {
			throw new CustomerValidationException(List.of("Only active customers can be deleted."));
		}

		LocalDateTime deletedAt = LocalDateTime.now();
		String actor = currentActor();
		customer.setStatus(CustomerStatus.DELETED);
		customer.setDeletedDate(deletedAt);
		customer.setDeletedBy(actor);
		customer.setUpdatedDate(deletedAt);
		customer.setUpdatedBy(actor);
		customerRepository.save(customer);
	}	
	@Override
	@Transactional
	public CustomerDetailsResponse addProductToCustomer(String customerId, String productId) {
    	Customer customer = customerRepository
            	.findByCustomerIdAndStatusNot(customerId, CustomerStatus.DELETED)
            	.orElseThrow(() -> new CustomerNotFoundException(customerId));
   	 	Product product = productRepository.findById(productId)
           	 	.orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    	product.setCustomerId(customer.getCustomerId());
    	productRepository.save(product);
    	return getCustomerDetails(customerId);
	}
}
