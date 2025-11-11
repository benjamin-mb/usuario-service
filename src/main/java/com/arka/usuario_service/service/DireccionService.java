package com.arka.usuario_service.service;

import com.arka.usuario_service.DTO.DtoAddressCreate;
import com.arka.usuario_service.Mapper.DireccionMapper;
import com.arka.usuario_service.excepcion.UsuarIoNotFound;
import com.arka.usuario_service.model.Direcciones;
import com.arka.usuario_service.model.Usuarios;
import com.arka.usuario_service.repositorio.DireccionesRepository;
import com.arka.usuario_service.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DireccionService {

    private final UsuarioRepository usuarioRepository;
    private final DireccionesRepository repository;
    private final DireccionMapper mapper;
    public DireccionService(UsuarioRepository usuarioRepository, DireccionesRepository repository, DireccionMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public Direcciones crearDireccion(Integer idUsuario, DtoAddressCreate dto) {

        Usuarios usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarIoNotFound("User not found with id: " + idUsuario));

        if ("administrador".equalsIgnoreCase(usuario.getTipo().toString())) {
            throw new IllegalArgumentException("Admins cannot have addresses");
        }

        Direcciones direccion= mapper.toDomain(dto);
        validarCampos(direccion);

        if (direccion.isPrincipal()) {
            repository.removeAllPrincipalFlags(idUsuario);
        }

        List<Direcciones> direccionesExistentes = repository.findAllByUsuario_Id(idUsuario);
        if (direccionesExistentes.isEmpty()) {
            direccion.setPrincipal(true);
        }

        direccion.setUsuario(usuario);

        return repository.save(direccion);
    }

    @Transactional(readOnly = true)
    public List<Direcciones> obtenerDirecciones(Integer idUsuario) {
        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + idUsuario));

        return repository.findAllByUsuario_Id(idUsuario);
    }

    @Transactional(readOnly = true)
    public Direcciones obtenerDireccionPrincipal(Integer idUsuario) {
        return repository.findByUsuario_IdAndPrincipal(idUsuario, true)
                .orElseThrow(() -> new IllegalArgumentException("No principal address found for user: " + idUsuario));
    }

    @Transactional
    public Direcciones obtenerDireccionPorIdUsuarioYIdDireccion(Integer idUsuario,Integer idDireccion){
        return repository.findByUsuario_IdAndId(idUsuario,idDireccion)
                .orElseThrow(()->new IllegalArgumentException("id not found by id: "+idDireccion));
    }
    @Transactional
    public Direcciones actualizarDireccion(Integer idDireccion, DtoAddressCreate dto) {

        Direcciones direccionExistente = repository.findById(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + idDireccion));


        Direcciones direccionActualizada=mapper.toDomain(dto);
        validarCampos(direccionActualizada);

        direccionExistente.setPais(direccionActualizada.getPais());
        direccionExistente.setDepartamento(direccionActualizada.getDepartamento());
        direccionExistente.setDireccion(direccionActualizada.getDireccion());


        if (direccionActualizada.isPrincipal() && !direccionExistente.isPrincipal()) {
            repository.removeAllPrincipalFlags(direccionExistente.getUsuario().getId());
            direccionExistente.setPrincipal(true);
        }

        return repository.save(direccionExistente);
    }

    @Transactional
    public void eliminarDireccion(Integer idDireccion) {
        Direcciones direccion = repository.findById(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + idDireccion));

        Integer idUsuario = direccion.getUsuario().getId();

        repository.delete(direccion);

        if (direccion.isPrincipal()) {
            List<Direcciones> restantes = repository.findAllByUsuario_Id(idUsuario);
            if (!restantes.isEmpty()) {
                Direcciones primera = restantes.get(0);
                primera.setPrincipal(true);
                repository.save(primera);
            }
        }
    }

    private void validarCampos(Direcciones direccion) {
        if (direccion.getCiudad() == null || direccion.getCiudad().isBlank()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (direccion.getPais() == null || direccion.getPais().isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }
        if (direccion.getDepartamento() == null || direccion.getDepartamento().isBlank()) {
            throw new IllegalArgumentException("Department cannot be null or empty");
        }

        if (direccion.getDireccion() == null || direccion.getDireccion().isBlank()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
    }
}
