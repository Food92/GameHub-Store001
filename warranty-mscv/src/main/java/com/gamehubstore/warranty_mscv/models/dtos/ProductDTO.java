package com.gamehubstore.warranty_mscv.models.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Long productId;
    private String nombre;
    private Integer mesesGarantia; // Clave para saber el plazo límite del producto
}