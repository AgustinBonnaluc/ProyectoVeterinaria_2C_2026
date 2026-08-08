package com.vetSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//@Getter
//@Setter

@Entity
@Data
@AllArgsConstructor //genera un constructor con todos los args
@NoArgsConstructor //constructor sin argumentos

@Table(name = "duenios")
public class Duenio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincremental
    private Long id;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(nullable = false, unique = true)
    private String cedula;
    @Column(nullable = false)
    private int telefono;
    @Column(nullable = false)
    private String email;

    @OneToMany (mappedBy = "duenio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mascota> mascotas;
}
