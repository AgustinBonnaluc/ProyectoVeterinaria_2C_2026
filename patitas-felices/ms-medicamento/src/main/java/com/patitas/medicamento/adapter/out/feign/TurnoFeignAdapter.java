package com.patitas.medicamento.adapter.out.feign;

import com.patitas.medicamento.domain.exception.ServicioNoDisponibleException;
import com.patitas.medicamento.domain.port.out.TurnoServicePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TurnoFeignAdapter implements TurnoServicePort {

    private final TurnoFeignClient client;

    @Override
    public Optional<String> buscarEstado(Long turnoId) {
        try {
            return Optional.of(client.findById(turnoId).getEstado());
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("El servicio de turnos no está disponible");
        }
    }
}
