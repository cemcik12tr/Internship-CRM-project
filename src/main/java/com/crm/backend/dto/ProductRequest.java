package com.crm.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class ProductRequest {
    
    @NotBlank(message = "Catalog ID boş bırakılamaz")
    private String catalogId;

    @NotBlank(message = "Ürün adı boş bırakılamaz")
    @Size(min = 2, max = 100, message = "Ürün adı 2 ile 100 karakter arasında olmalıdır")
    private String name;

    @NotNull(message = "Ürün ücreti boş bırakılamaz")
    @DecimalMin(value = "0.0" , inclusive = true , message = "Ürün fiyatı negatif olamaz")
    @Digits(integer = 8, fraction = 2,message = "Fiyat en fazla 2 ondalık basamak içerebilir")
    private BigDecimal price;
    
    @NotNull(message = "Stok boş bırakılamaz")
    @Min(value = 0, message = "Stok negatif olamaz")
    private Integer stock;

}
