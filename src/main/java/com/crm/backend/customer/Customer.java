package com.crm.backend.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_id", nullable = false, unique = true, updatable = false)
	private String customerId;

	@Column(name = "national_id", nullable = false, length = 11)
	private String nationalId;

	@Column(name = "gsm_number", nullable = false, length = 10)
	private String gsmNumber;

	@Column(name = "account_number", unique = true, updatable = false)
	private String accountNumber;

	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "middle_name", length = 50)
	private String middleName;

	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CustomerStatus status;

	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "created_by", nullable = false, updatable = false, length = 100)
	private String createdBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "deleted_date")
	private LocalDateTime deletedDate;

	@Column(name = "updated_by", length = 100)
	private String updatedBy;

	@Column(name = "deleted_by", length = 100)
	private String deletedBy;

	@PrePersist
	void prePersist() {
		if (status == null) {
			status = CustomerStatus.ACTIVE;
		}
		if (createdDate == null) {
			createdDate = LocalDateTime.now();
		}
		if (createdBy == null || createdBy.isBlank()) {
			createdBy = "system";
		}
	}

	public Long getId() {
		return id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getGsmNumber() {
		return gsmNumber;
	}

	public void setGsmNumber(String gsmNumber) {
		this.gsmNumber = gsmNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public CustomerStatus getStatus() {
		return status;
	}

	public void setStatus(CustomerStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public LocalDateTime getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(LocalDateTime deletedDate) {
		this.deletedDate = deletedDate;
	}
	public String getUpdatedBy() {
    	return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
    	this.updatedBy = updatedBy;
	}
	public String getDeletedBy() {
    	return deletedBy;
	}
	public void setDeletedBy(String deletedBy) {
    	this.deletedBy = deletedBy;
	}

}
