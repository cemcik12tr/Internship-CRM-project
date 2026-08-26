package com.crm.backend.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crm.backend.customer.dto.CreateCustomerRequest;
import com.crm.backend.customer.dto.CreateCustomerResponse;
import com.crm.backend.customer.dto.CustomerDetailsResponse;
import com.crm.backend.customer.dto.CustomerSearchCriteria;
import com.crm.backend.customer.dto.CustomerSearchResultResponse;
import com.crm.backend.customer.dto.UpdateCustomerRequest;
import com.crm.backend.model.Product;
import com.crm.backend.model.enums.Status;
import com.crm.backend.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CustomerServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private ProductRepository productRepository;

	private CustomerService customerService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		customerService = new CustomerServiceImpl(customerRepository, productRepository);
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

	@Test
	void shouldSearchCustomersWithTheProvidedFilters() {
		Customer customer = customer("CUST100", "ACC100", "Duygu", null, "Yunus");
		when(customerRepository.searchCustomers(
				"CUST100", null, null, null, "duy", null, null, CustomerStatus.DELETED
		)).thenReturn(List.of(customer));

		List<CustomerSearchResultResponse> results = customerService.searchCustomers(new CustomerSearchCriteria(
				" CUST100 ", null, null, null, " duy ", null, null
		));

		assertThat(results).containsExactly(new CustomerSearchResultResponse(
				"CUST100", "Duygu Yunus", "ACC100", "5551234567", "ACTIVE"
		));
	}

	@Test
	void shouldReturnCustomerDetailsWithActiveProducts() {
		Customer customer = customer("CUST100", "ACC100", "Duygu", "Ayse", "Yunus");
		Product product = new Product();
		product.setId("15");
		product.setName("Credit Card");
		product.setPrice(java.math.BigDecimal.valueOf(450.00));
		product.setStatus(Status.ACTIVE);

		when(customerRepository.findByCustomerIdAndStatusNot("CUST100", CustomerStatus.DELETED))
				.thenReturn(Optional.of(customer));
		when(productRepository.findByCustomerIdAndStatus("CUST100", Status.ACTIVE)).thenReturn(List.of(product));

		CustomerDetailsResponse details = customerService.getCustomerDetails("CUST100");

		assertThat(details.customerId()).isEqualTo("CUST100");
		assertThat(details.firstName()).isEqualTo("Duygu");
		assertThat(details.middleName()).isEqualTo("Ayse");
		assertThat(details.products()).hasSize(1);
		assertThat(details.products().get(0).productName()).isEqualTo("Credit Card");
		assertThat(details.products().get(0).status()).isEqualTo("ACTIVE");
	}

	@Test
	void shouldNotReturnSoftDeletedCustomerDetails() {
		when(customerRepository.findByCustomerIdAndStatusNot("CUST404", CustomerStatus.DELETED))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> customerService.getCustomerDetails("CUST404"))
				.isInstanceOf(CustomerNotFoundException.class)
				.hasMessage("Customer not found: CUST404");
	}

	@Test
	void shouldUpdateEditableCustomerInformation() {
		Customer customer = customer("CUST100", "ACC100", "Duygu", null, "Yunus");
		UpdateCustomerRequest request = new UpdateCustomerRequest(
				"5551234568",
				"Duygu",
				"Ayse",
				"Yilmaz",
				CustomerStatus.INACTIVE
		);

		when(customerRepository.findByCustomerIdAndStatusNot("CUST100", CustomerStatus.DELETED))
				.thenReturn(Optional.of(customer));
		when(customerRepository.existsDuplicateGsm(
				"5551234568", "CUST100", List.of(CustomerStatus.ACTIVE, CustomerStatus.DELETED)
		)).thenReturn(false);
		when(productRepository.findByCustomerIdAndStatus("CUST100", Status.ACTIVE)).thenReturn(List.of());

		CustomerDetailsResponse response = customerService.updateCustomer("CUST100", request);

		assertThat(customer.getCustomerId()).isEqualTo("CUST100");
		assertThat(customer.getAccountNumber()).isEqualTo("ACC100");
		assertThat(customer.getNationalId()).isEqualTo("12345678901");
		assertThat(customer.getGsmNumber()).isEqualTo("5551234568");
		assertThat(customer.getMiddleName()).isEqualTo("Ayse");
		assertThat(customer.getLastName()).isEqualTo("Yilmaz");
		assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
		assertThat(customer.getUpdatedDate()).isNotNull();
		assertThat(customer.getUpdatedBy()).isEqualTo("system");
		assertThat(response.status()).isEqualTo("INACTIVE");
		verify(customerRepository).save(customer);
	}

	@Test
	void shouldRejectDeletedStatusDuringUpdate() {
		Customer customer = customer("CUST100", "ACC100", "Duygu", null, "Yunus");
		UpdateCustomerRequest request = new UpdateCustomerRequest(
				"5551234567",
				"Duygu",
				null,
				"Yunus",
				CustomerStatus.DELETED
		);

		when(customerRepository.findByCustomerIdAndStatusNot("CUST100", CustomerStatus.DELETED))
				.thenReturn(Optional.of(customer));

		assertThatThrownBy(() -> customerService.updateCustomer("CUST100", request))
				.isInstanceOf(CustomerValidationException.class)
				.hasMessage("Customer information is not valid.");
	}

	@Test
	void shouldSoftDeleteAnActiveCustomer() {
		Customer customer = customer("CUST100", "ACC100", "Duygu", null, "Yunus");

		when(customerRepository.findByCustomerIdAndStatusNot("CUST100", CustomerStatus.DELETED))
				.thenReturn(Optional.of(customer));

		customerService.softDeleteCustomer("CUST100");

		assertThat(customer.getStatus()).isEqualTo(CustomerStatus.DELETED);
		assertThat(customer.getDeletedDate()).isNotNull();
		assertThat(customer.getDeletedBy()).isEqualTo("system");
		assertThat(customer.getUpdatedDate()).isEqualTo(customer.getDeletedDate());
		assertThat(customer.getUpdatedBy()).isEqualTo("system");
		verify(customerRepository).save(customer);
	}

	@Test
	void shouldRejectSoftDeleteForAnInactiveCustomer() {
		Customer customer = customer("CUST100", "ACC100", "Duygu", null, "Yunus");
		customer.setStatus(CustomerStatus.INACTIVE);

		when(customerRepository.findByCustomerIdAndStatusNot("CUST100", CustomerStatus.DELETED))
				.thenReturn(Optional.of(customer));

		assertThatThrownBy(() -> customerService.softDeleteCustomer("CUST100"))
				.isInstanceOf(CustomerValidationException.class)
				.hasMessage("Customer information is not valid.");
	}

	private Customer customer(String customerId, String accountNumber, String firstName, String middleName, String lastName) {
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setAccountNumber(accountNumber);
		customer.setNationalId("12345678901");
		customer.setGsmNumber("5551234567");
		customer.setFirstName(firstName);
		customer.setMiddleName(middleName);
		customer.setLastName(lastName);
		customer.setStatus(CustomerStatus.ACTIVE);
		customer.setCreatedDate(LocalDateTime.of(2026, 8, 20, 10, 0));
		customer.setCreatedBy("system");
		return customer;
	}
}
