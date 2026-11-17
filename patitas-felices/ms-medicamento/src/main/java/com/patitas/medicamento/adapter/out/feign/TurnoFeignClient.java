package com.patitas.medicamento.adapter.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-turno")
public interface TurnoFeignClient {

    @GetMapping(value = "/api/turnos/{id}", produces = "application/json")
    TurnoResponse findById(@PathVariable("id") Long id);
}
