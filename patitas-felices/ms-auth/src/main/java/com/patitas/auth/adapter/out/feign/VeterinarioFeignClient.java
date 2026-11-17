package com.patitas.auth.adapter.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-veterinario")
public interface VeterinarioFeignClient {

    @GetMapping(value = "/api/veterinarios/matricula/{matricula}", produces = "application/json")
    VeterinarioResponse findByMatricula(@PathVariable("matricula") String matricula);
}
