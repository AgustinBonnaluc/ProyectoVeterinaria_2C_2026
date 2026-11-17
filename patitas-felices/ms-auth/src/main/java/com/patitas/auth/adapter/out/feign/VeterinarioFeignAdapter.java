package com.patitas.auth.adapter.out.feign;

import com.patitas.auth.domain.exception.ServicioNoDisponibleException;
import com.patitas.auth.domain.port.out.VeterinarioServicePort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VeterinarioFeignAdapter implements VeterinarioServicePort {

    private final VeterinarioFeignClient client;

    @Override
    public Optional<Long> buscarIdPorMatricula(String matricula) {
        try {
            return Optional.of(client.findByMatricula(matricula).getId());
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("El servicio de veterinarios no está disponible");
        }
    }
}
