package com.vetSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turnos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(nullable = false)
    private LocalTime hora;
    @Column(nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTurno estado = EstadoTurno.PENDIENTE;

    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id",nullable = false)
    private Veterinario veterinario;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "turno_medicamento",
            joinColumns = @JoinColumn(name = "turno_id"),
            inverseJoinColumns = @JoinColumn(name = "medicamento_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Medicamento> medicamentos = new ArrayList<>();
}
