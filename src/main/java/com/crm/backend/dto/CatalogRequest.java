package com.crm.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CatalogRequest {
    @NotBlank(message = "Catalog name is mandatorty and cannot be just spaces.")
    @Size(min = 2, max = 100, message = "Catalog name must be between 2 and 100 characters."  )
    private String name ;
    
}