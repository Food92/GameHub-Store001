package com.gamehubstore.warranty_mscv.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String estado;         // Debe estar en "PAGADA" o "ENTREGADA"
    private LocalDateTime fechaCompra; // Clave para validar el plazo de garantía
}
