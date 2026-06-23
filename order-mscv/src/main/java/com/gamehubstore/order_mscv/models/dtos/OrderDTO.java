package com.gamehubstore.order_mscv.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;

    @NotNull(message = "El idUsuario es obligatorio")
    private Long idUsuario; // Cambiado para coincidir con la implementación del service

    @PositiveOrZero(message = "El descuento debe ser mayor o igual a 0")
    private Double descuento; // Opcional

    @NotBlank(message = "El estado no puede ser vacío")
    private String estado;

    @PositiveOrZero(message = "El subtotal debe ser mayor o igual a 0")
    private Double subtotal;

    @PositiveOrZero(message = "El total debe ser mayor o igual a 0")
    private Double total;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion; // Añadido para capturar la fecha que retorna el servicio

    @NotEmpty(message = "La orden debe contener al menos un detalle de producto")
    @Valid // CRÍTICO: Obliga a Spring a validar las restricciones internas de cada DetailOrderDTO
    private List<DetailOrderDTO> details;
}