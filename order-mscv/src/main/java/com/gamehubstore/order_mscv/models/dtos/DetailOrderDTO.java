package com.gamehubstore.order_mscv.models.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class DetailOrderDTO {

    @NotNull(message = "El idProducto es obligatorio en el detalle")
    private Long idProduct;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad mínima de un producto debe ser 1")
    private Long cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a 0")
    private Double precioUnitario;

    @NotNull(message = "El precio total del ítem es obligatorio")
    @Positive(message = "El precio total por ítem debe ser mayor a 0")
    private Double precioTotal; // Representa el cálculo de: cantidad * precioUnitario
}