package com.patitas.turno.adapter.out.feign;

import com.patitas.turno.domain.exception.ServicioNoDisponibleException;
import com.patitas.turno.domain.port.out.MascotaServicePort;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MascotaFeignAdapter implements MascotaServicePort {

    private final MascotaFeignClient client;

    @Override
    @CircuitBreaker(name = "mascotaService", fallbackMethod = "fallbackMascota")
    @Retry(name = "mascotaService", fallbackMethod = "fallbackMascota")
    public boolean existsById(Long id) {
        try {
            client.findById(id);
            return true;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

    private boolean fallbackMascota(Long id, Throwable ex) {
        throw new ServicioNoDisponibleException("El servicio de mascotas no está disponible");
    }
}
