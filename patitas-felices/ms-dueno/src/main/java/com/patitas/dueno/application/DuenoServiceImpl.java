package com.patitas.dueno.application;

import com.patitas.dueno.domain.exception.DuenoNotFoundException;
import com.patitas.dueno.domain.exception.DuplicatedEmailException;
import com.patitas.dueno.domain.model.Dueno;
import com.patitas.dueno.domain.port.in.DuenoService;
import com.patitas.dueno.domain.port.out.DuenoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuenoServiceImpl implements DuenoService {

    private final DuenoRepositoryPort duenoRepository;

    @Override
    public List<Dueno> findAll() {
        return duenoRepository.findAll();
    }

    @Override
    public Dueno findById(Long id) {
        return duenoRepository.findById(id)
                .orElseThrow(() -> new DuenoNotFoundException(id));
    }

    // Si el email ya existe corta acá, si no lo guarda
    @Override
    public Dueno save(Dueno dueno) {
        if (duenoRepository.existsByEmail(dueno.getEmail())) {
            throw new DuplicatedEmailException(dueno.getEmail());
        }
        dueno.setId(null);
        return duenoRepository.save(dueno);
    }

    @Override
    public Dueno update(Long id, Dueno datos) {
        Dueno existente = findById(id);

        boolean cambioEmail = !existente.getEmail().equalsIgnoreCase(datos.getEmail());
        if (cambioEmail && duenoRepository.existsByEmail(datos.getEmail())) {
            throw new DuplicatedEmailException(datos.getEmail());
        }

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        return duenoRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!duenoRepository.existsById(id)) {
            throw new DuenoNotFoundException(id);
        }
        duenoRepository.deleteById(id);
    }
}
