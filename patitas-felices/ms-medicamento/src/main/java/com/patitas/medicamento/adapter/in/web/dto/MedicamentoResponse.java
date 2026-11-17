package com.patitas.medicamento.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoResponse {

    private Long id;
    private String nombre;
    private String principioActivo;
    private Integer stock;
    private BigDecimal precioUnitario;
}
