package com.vetSystem.Controller;

import com.vetSystem.Entity.Duenio;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Service.DuenioService;
import com.vetSystem.Service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/duenio")
public class DuenioController {

    @Autowired
    private DuenioService duenioService;
    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<Duenio>> buscarTodosLosDuenios(){

        return ResponseEntity.ok(duenioService.listarTodos());
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<?> listarMascotasDelDuenio(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mascotaService.listarPorDuenio(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Duenio> buscarPorId(@PathVariable Long id){

        try {
            return ResponseEntity.ok(duenioService.buscarPorId(id));

        } catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }

    }


    @PostMapping
    public ResponseEntity<?> crearDuenio(@RequestBody Duenio duenio){

        try{
            Duenio nuevo = duenioService.registrarDuenio(duenio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); //devuelve status http 201
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); //409

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Duenio> actualizarDuenio(@PathVariable Long id, @RequestBody Duenio duenio){
        try{
            return ResponseEntity.ok(duenioService.modificarDuenio(id,duenio));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build(); //409

        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDuenio(@PathVariable Long id){
        try{

            duenioService.eliminarDuenio(id);
            return ResponseEntity.noContent().build(); //204

        } catch (ResourceNotFoundException e){

            return ResponseEntity.notFound().build();

        }
    }

}
