package com.vetSystem.Service;

import com.vetSystem.Entity.Duenio;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Repository.DuenioRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class DuenioService {

    @Autowired //
    private final DuenioRepository duenioRepository;

    public List<Duenio> listarTodos(){
        return duenioRepository.findAll();
    }

    //saco el Optional para no tener que manejarlo, o devuelve un Dueño o lanza excepcion. (preguntar)
    public Duenio buscarPorId(Long id){

        return duenioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dueño",id));

    }

    @Transactional
    public Duenio registrarDuenio(Duenio duenio){

        if (duenioRepository.existsByCedula(duenio.getCedula())){

            throw new DuplicateResourceException("Dueño",duenio.getCedula());
        }else{
            return duenioRepository.save(duenio);
        }

    }

    public Duenio modificarDuenio(Long id, Duenio duenioActualizado){
        Duenio duenio = buscarPorId(id);
        duenio.setNombre(duenioActualizado.getNombre());
        duenio.setApellido(duenioActualizado.getApellido());
        duenio.setEmail(duenioActualizado.getEmail());
        duenio.setTelefono(duenioActualizado.getTelefono());

        return duenioRepository.save(duenio);
    }

    public void eliminarDuenio(Long id){
        Duenio duenio = buscarPorId(id);
        duenioRepository.delete(duenio);
    }

    public Optional<Duenio> buscarPorNombre(String nombre){
        return duenioRepository.findByNombre(nombre);

    }

}




