package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.ConfiguracionCronometroRequestDTO;
import com.boxing.api.controller.dto.ConfiguracionCronometroResponseDTO;
import com.boxing.api.model.ConfiguracionCronometro;
import com.boxing.api.model.Usuario;
import com.boxing.api.repository.ConfiguracionCronometroRepository;
import com.boxing.api.repository.UsuarioRepository;
import com.boxing.api.service.ConfiguracionCronometroService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ConfiguracionCronometroServiceImpl implements ConfiguracionCronometroService {

    private final ConfiguracionCronometroRepository cronometroRepository;
    private final UsuarioRepository usuarioRepository;

    public ConfiguracionCronometroServiceImpl(ConfiguracionCronometroRepository cronometroRepository,
                                              UsuarioRepository usuarioRepository) {
        this.cronometroRepository = cronometroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionCronometroResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return cronometroRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConfiguracionCronometroResponseDTO crear(ConfiguracionCronometroRequestDTO dto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        ConfiguracionCronometro config = new ConfiguracionCronometro(
                dto.getNombre(), dto.getRondas(), dto.getDuracionRonda(), dto.getDescanso(), usuario
        );

        return toResponse(cronometroRepository.save(config));
    }

    @Override
    @Transactional
    public ConfiguracionCronometroResponseDTO actualizar(Long id, ConfiguracionCronometroRequestDTO dto, Long usuarioId) {
        ConfiguracionCronometro config = cronometroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Configuración no encontrada"));

        config.setNombre(dto.getNombre());
        config.setRondas(dto.getRondas());
        config.setDuracionRonda(dto.getDuracionRonda());
        config.setDescanso(dto.getDescanso());

        return toResponse(cronometroRepository.save(config));
    }

    @Override
    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        ConfiguracionCronometro config = cronometroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Configuración no encontrada"));

        cronometroRepository.delete(config);
    }

    private ConfiguracionCronometroResponseDTO toResponse(ConfiguracionCronometro config) {
        return new ConfiguracionCronometroResponseDTO(
                config.getId(),
                config.getNombre(),
                config.getRondas(),
                config.getDuracionRonda(),
                config.getDescanso()
        );
    }
}
