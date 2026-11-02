package com.patitas.veterinario.application;

import com.patitas.veterinario.domain.exception.DuplicatedMatriculaException;
import com.patitas.veterinario.domain.exception.VeterinarioNotFoundException;
import com.patitas.veterinario.domain.model.Veterinario;
import com.patitas.veterinario.domain.port.in.VeterinarioService;
import com.patitas.veterinario.domain.port.out.VeterinarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepositoryPort veterinarioRepository;

    @Override
    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    @Override
    public Veterinario findById(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new VeterinarioNotFoundException(id));
    }

    @Override
    public List<Veterinario> findByEspecialidad(String especialidad) {
        return veterinarioRepository.findByEspecialidadIgnoreCase(especialidad);
    }

    @Override
    public Veterinario save(Veterinario veterinario) {
        if (veterinarioRepository.existsByMatricula(veterinario.getMatricula())) {
            throw new DuplicatedMatriculaException(veterinario.getMatricula());
        }
        veterinario.setId(null);
        return veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario update(Long id, Veterinario datos) {
        Veterinario existente = findById(id);

        boolean cambioMatricula = !existente.getMatricula().equalsIgnoreCase(datos.getMatricula());
        if (cambioMatricula && veterinarioRepository.existsByMatricula(datos.getMatricula())) {
            throw new DuplicatedMatriculaException(datos.getMatricula());
        }

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setMatricula(datos.getMatricula());
        existente.setEspecialidad(datos.getEspecialidad());
        existente.setEmail(datos.getEmail());
        return veterinarioRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!veterinarioRepository.existsById(id)) {
            throw new VeterinarioNotFoundException(id);
        }
        veterinarioRepository.deleteById(id);
    }
}
