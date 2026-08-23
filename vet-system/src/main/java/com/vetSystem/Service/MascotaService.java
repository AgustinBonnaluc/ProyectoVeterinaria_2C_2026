package com.vetSystem.Service;


import com.vetSystem.Entity.Duenio;
import com.vetSystem.Entity.Mascota;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Repository.MascotaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenioService duenioService;


    @Transactional
    public Mascota guardarMascota(Mascota mascota, Long duenioId){
        Duenio duenio = duenioService.buscarPorId(duenioId);

        if (mascotaRepository.existsByNombreAndDuenioId(mascota.getNombre(), duenioId)) {
            throw new DuplicateResourceException("Mascota", mascota.getNombre());
        }

        mascota.setDuenio(duenio);
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota modificarMascota(Long id, Mascota mascotaActualizada, Long duenioId) {
        Mascota mascota = buscarPorId(id);

        mascota.setNombre(mascotaActualizada.getNombre());
        mascota.setEspecie(mascotaActualizada.getEspecie());
        mascota.setRaza(mascotaActualizada.getRaza());
        mascota.setFechaNacimiento(mascotaActualizada.getFechaNacimiento());

        if (duenioId != null) {
            mascota.setDuenio(duenioService.buscarPorId(duenioId));
        }

        return mascotaRepository.save(mascota);
    }


    @Transactional
    public void eliminarMascota(Long id){
        Mascota mascota = buscarPorId(id);
        mascotaRepository.delete(mascota);
    }

    public Mascota buscarPorId(Long id){
        return mascotaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Mascota",id));
    }

    public List<Mascota> listarTodos(){
        return mascotaRepository.findAll();
    }

    public List<Mascota> listarPorDuenio(Long duenioId) {
        duenioService.buscarPorId(duenioId);
        return mascotaRepository.findByDuenioId(duenioId);
    }

}
