package com.vetSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String email;

    // Sin List<Mascota>: las mascotas se consultan por GET /api/duenio/{id}/mascotas
}
