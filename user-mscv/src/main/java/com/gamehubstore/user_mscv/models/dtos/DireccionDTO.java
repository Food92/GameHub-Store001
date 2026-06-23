package com.gamehubstore.user_mscv.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class DireccionDTO {
    private Long direccionId;
    private Long userId;
    private String comuna;
    private String ciudad;
    private String calle;
    private String numero;
}
