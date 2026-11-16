package com.patitas.turno.adapter.out.feign;

import com.patitas.turno.domain.exception.ServicioNoDisponibleException;
import com.patitas.turno.domain.port.out.VeterinarioServicePort;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeterinarioFeignAdapter implements VeterinarioServicePort {

    private final VeterinarioFeignClient client;

    @Override
    @CircuitBreaker(name = "veterinarioService", fallbackMethod = "fallbackVeterinario")
    @Retry(name = "veterinarioService", fallbackMethod = "fallbackVeterinario")
    public boolean existsById(Long id) {
        try {
            client.findById(id);
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    private boolean fallbackVeterinario(Long id, Throwable ex) {
        throw new ServicioNoDisponibleException("El servicio de veterinarios no está disponible");
    }
}
