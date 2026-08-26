package com.crm.backend.customer.dto;

public record CustomerSearchCriteria(
        String customerId,
        String nationalId,
        String gsmNumber,
        String accountNumber,
        String firstName,
        String middleName,
        String lastName
) {

    public CustomerSearchCriteria normalized() {
        return new CustomerSearchCriteria(
                trimToNull(customerId),
                trimToNull(nationalId),
                trimToNull(gsmNumber),
                trimToNull(accountNumber),
                trimToLowerCase(firstName),
                trimToLowerCase(middleName),
                trimToLowerCase(lastName)
        );
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String trimToLowerCase(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase();
    }
}
