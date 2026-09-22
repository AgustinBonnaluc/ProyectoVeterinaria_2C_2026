package com.vetSystem.Exception;

import java.time.LocalDate;
import java.time.LocalTime;

public class TurnoSuperpuestoException extends RuntimeException {

    public TurnoSuperpuestoException(String mensaje) {
        super(mensaje);
    }

    public TurnoSuperpuestoException(Long veterinarioId, LocalDate fecha, LocalTime hora) {
        super("El veterinario con id " + veterinarioId
                + " ya tiene un turno agendado el " + fecha + " a las " + hora);
    }

    public TurnoSuperpuestoException(Long veterinarioId, Long turnoConflictivoId,
                                     LocalDate fecha, LocalTime hora) {
        super("El veterinario con id " + veterinarioId + " ya tiene el turno con id "
                + turnoConflictivoId + " agendado el " + fecha + " a las " + hora + ".");
    }

}
