package com.crm.backend.customer;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String NAME_PATTERN = "^[A-Za-zÇĞİÖŞÜçğıöşü ]{2,50}$";

	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
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
		customer.setMiddleName(normalizeOptional(request.middleName()));
		customer.setLastName(request.lastName().trim());
		customer.setStatus(CustomerStatus.ACTIVE);
		customer.setCreatedBy("system");

		Customer savedCustomer = customerRepository.save(customer);
		return new CreateCustomerResponse(
				savedCustomer.getCustomerId(),
				savedCustomer.getAccountNumber(),
				savedCustomer.getStatus().name()
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
		if (customerRepository.existsByNationalIdAndStatus(request.nationalId().trim(), CustomerStatus.ACTIVE)
				|| customerRepository.existsByGsmNumberAndStatus(request.gsmNumber().trim(), CustomerStatus.ACTIVE)) {
			throw new DuplicateCustomerException("Customer already exists.");
		}
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

	private String normalizeOptional(String value) {
		return isBlank(value) ? null : value.trim();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
