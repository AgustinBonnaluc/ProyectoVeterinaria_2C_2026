package com.vetSystem.Controller;


import com.vetSystem.Entity.Mascota;
import com.vetSystem.Service.MascotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/mascota")
public class MascotaController {

    private MascotaService mascotaService;

    public Mascota registrarMascota(Mascota mascota){
        return mascotaService.registrarMascota(mascota);
    }

    public ResponseEntity<Optional<Mascota>> buscarPorId(Long id){
        return ResponseEntity.ok(mascotaService.buscarPorId(id));
    }


}
