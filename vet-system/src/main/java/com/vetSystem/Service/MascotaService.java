package com.vetSystem.Service;


import com.vetSystem.Entity.Mascota;
import com.vetSystem.Repository.MascotaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))

public class MascotaService {

    @Autowired
    private final MascotaRepository mascotaRepository;

    public Mascota registrarMascota(Mascota mascota){
        return mascotaRepository.save(mascota);
    }

    public Optional<Mascota> buscarPorId(Long id){
        return mascotaRepository.findById(id);
    }

    public List<Mascota> listarTodos(){
        return mascotaRepository.findAll();
    }

    public void eliminarMascota(Long id){
        mascotaRepository.deleteById(id);
    }



}
