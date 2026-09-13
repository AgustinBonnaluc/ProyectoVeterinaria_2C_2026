package com.vetSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {

    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;

    private Long duenioId;        // el ID, no el objeto Duenio completo
    private String duenioNombre;  // desnormalizado, para que el cliente no tenga que hacer otra request
}