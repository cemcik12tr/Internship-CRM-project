package com.crm.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Please enter a valid email address.")
    @Size(min = 10, max = 250, message = "Please enter a valid email address.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Please enter a valid email address."
    )
    private String username;

    @NotBlank(message = "Password must contain at least 8 characters.")
    @Size(min = 8, max = 16, message = "Password must contain at least 8 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z0-9]{8,16}$",
            message = "Password must contain at least 1 uppercase, 1 lowercase, 1 number, and no special characters."
    )
    private String password;
}