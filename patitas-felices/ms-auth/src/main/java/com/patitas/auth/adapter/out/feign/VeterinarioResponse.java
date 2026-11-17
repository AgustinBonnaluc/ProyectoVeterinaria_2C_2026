package com.patitas.auth.adapter.out.feign;

import lombok.Data;

@Data
public class VeterinarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String matricula;
}
