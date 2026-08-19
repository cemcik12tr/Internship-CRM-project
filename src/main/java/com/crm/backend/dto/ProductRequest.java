package com.crm.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Ürün adı boş bırakılamaz")
    private String name;

    @NotNull(message = "Ürün ücreti boş bırakılamaz")
    @Min(value =0 , message = "Ürün fiyatı negatif olamaz")
    private Double price;

    @NotNull(message = "Stok adeti boş bırakılamaz")
    @Min(value=0, message = "stok adeti negatif olamaz")
    private Integer stock;
}
