package com.gamehubstore.product_mscv.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NotNull
@ToString
public class CategoryDTO {
    private Long idCategory;
    private String nombreCategory;
    private String descripcionCategory;
    private String estado; // Activa o Inactiva
}
