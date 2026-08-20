package com.crm.backend.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		customerService = new CustomerServiceImpl(customerRepository);
	}

	@Test
	void shouldCreateCustomerWhenRequestIsValidAndUnique() {
		CreateCustomerRequest request = new CreateCustomerRequest(
				"12345678901",
				"5551234567",
				"Duygu",
				null,
				"Yunus"
		);

		when(customerRepository.existsByNationalIdAndStatus("12345678901", CustomerStatus.ACTIVE)).thenReturn(false);
		when(customerRepository.existsByGsmNumberAndStatus("5551234567", CustomerStatus.ACTIVE)).thenReturn(false);
		when(customerRepository.existsByCustomerId(any(String.class))).thenReturn(false);
		when(customerRepository.existsByAccountNumber(any(String.class))).thenReturn(false);
		when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CreateCustomerResponse response = customerService.createCustomer(request);

		assertThat(response.customerId()).startsWith("CUST");
		assertThat(response.accountNumber()).startsWith("ACC");
		assertThat(response.status()).isEqualTo("ACTIVE");

		ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
		verify(customerRepository).save(customerCaptor.capture());

		Customer savedCustomer = customerCaptor.getValue();
		assertThat(savedCustomer.getNationalId()).isEqualTo("12345678901");
		assertThat(savedCustomer.getGsmNumber()).isEqualTo("5551234567");
		assertThat(savedCustomer.getAccountNumber()).startsWith("ACC");
		assertThat(savedCustomer.getFirstName()).isEqualTo("Duygu");
		assertThat(savedCustomer.getLastName()).isEqualTo("Yunus");
		assertThat(savedCustomer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
	}

	@Test
	void shouldRejectInvalidCustomerInformation() {
		CreateCustomerRequest request = new CreateCustomerRequest(
				"123",
				"15551234567",
				"D",
				"1",
				""
		);

		assertThatThrownBy(() -> customerService.createCustomer(request))
				.isInstanceOf(CustomerValidationException.class)
				.hasMessage("Customer information is not valid.");
	}

	@Test
	void shouldRejectDuplicateActiveCustomer() {
		CreateCustomerRequest request = new CreateCustomerRequest(
				"12345678901",
				"5551234567",
				"Duygu",
				null,
				"Yunus"
		);

		when(customerRepository.existsByNationalIdAndStatus("12345678901", CustomerStatus.ACTIVE)).thenReturn(true);

		assertThatThrownBy(() -> customerService.createCustomer(request))
				.isInstanceOf(DuplicateCustomerException.class)
				.hasMessage("Customer already exists.");
	}
}
