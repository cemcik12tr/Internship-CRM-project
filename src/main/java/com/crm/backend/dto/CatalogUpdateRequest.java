package com.crm.backend.dto;

import com.crm.backend.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CatalogUpdateRequest {
    
    @NotBlank(message = "Catalog name is mandatory and cannot be just spaces.")
    @Size(min = 2, max = 100 , message = "Catalog name must be between 2 and 100 characters.")
    private String name;

    @NotNull(message = "Status is mandatory")
    private Status status;

}
