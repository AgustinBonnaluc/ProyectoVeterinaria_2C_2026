package com.vetSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "medicamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String principioActivo;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

}
