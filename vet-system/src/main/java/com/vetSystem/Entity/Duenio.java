package com.vetSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "duenios")
public class Duenio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Column(nullable = false, unique = true)
    private String cedula;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String email;

    @OneToMany(mappedBy = "duenio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude          // evita el toString() recursivo Duenio → Mascota → Duenio
    @EqualsAndHashCode.Exclude // evita el hashCode() recursivo
    private List<Mascota> mascotas;
}
