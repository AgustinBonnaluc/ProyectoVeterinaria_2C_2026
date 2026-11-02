package com.patitas.mascota.application;

import com.patitas.mascota.domain.exception.MascotaNotFoundException;
import com.patitas.mascota.domain.model.Mascota;
import com.patitas.mascota.domain.port.in.MascotaService;
import com.patitas.mascota.domain.port.out.MascotaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepositoryPort mascotaRepository;

    @Override
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    @Override
    public Mascota findById(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new MascotaNotFoundException(id));
    }

    @Override
    public List<Mascota> findByDuenoId(Long duenoId) {
        return mascotaRepository.findByDuenoId(duenoId);
    }

    @Override
    public Mascota save(Mascota mascota) {
        mascota.setId(null);
        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota update(Long id, Mascota datos) {
        Mascota existente = findById(id);
        existente.setNombre(datos.getNombre());
        existente.setEspecie(datos.getEspecie());
        existente.setRaza(datos.getRaza());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setDuenoId(datos.getDuenoId());
        return mascotaRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new MascotaNotFoundException(id);
        }
        mascotaRepository.deleteById(id);
    }
}
