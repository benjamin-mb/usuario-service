package com.arka.usuario_service.DireccionService;


import com.arka.usuario_service.DTO.DtoAddressCreate;
import com.arka.usuario_service.Mapper.DireccionMapper;
import com.arka.usuario_service.excepcion.UsuarIoNotFound;
import com.arka.usuario_service.model.Direcciones;
import com.arka.usuario_service.model.UserType;
import com.arka.usuario_service.model.Usuarios;
import com.arka.usuario_service.repositorio.DireccionesRepository;
import com.arka.usuario_service.repositorio.UsuarioRepository;
import com.arka.usuario_service.service.DireccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("test de direcciones")
public class DireccionTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DireccionesRepository repository;

    @Mock
    private DireccionMapper mapper;

    @InjectMocks
    private DireccionService service;

    private Integer idUsuario;
    private DtoAddressCreate dtoAddressCreate;
    private Direcciones direccion;
    private Usuarios usuario;

    @BeforeEach
    void setUp(){
        idUsuario=1;
        usuario=new Usuarios("Benjamin","benjamin0@gmail.com","Password@123!");
        usuario.setId(idUsuario);
        dtoAddressCreate=new DtoAddressCreate("Colombia","Medellin","Antioquia","av 33#54",true);
        direccion=new Direcciones(1,usuario,"Medellin","Colombia","Antioquia","av 33#54",true);
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 1: CREAR DIRECCION
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN01 -creacion de direccion exitosa")
    void creacionDireccionExitosa(){

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        direccion.setUsuario(usuario);
        when(mapper.toDomain(any(DtoAddressCreate.class))).thenReturn(direccion);
        when(repository.findAllByUsuario_Id(1)).thenReturn(Collections.EMPTY_LIST);
        when(repository.save(direccion)).thenReturn(direccion);

        Direcciones direccionResultado=service.crearDireccion(1,dtoAddressCreate);

        assertNotNull(direccionResultado);
        assertEquals("Medellin",direccionResultado.getCiudad());

        verify(usuarioRepository,times(1)).findById(1);
        verify(repository,times(1)).save(any(Direcciones.class));
    }

    @Test
    @DisplayName("LN02 -creacion de direccion fallida por usuario id")
    void creacionFallidaPorUserId(){

        when(usuarioRepository.findById(9)).thenReturn(Optional.empty());
        UsuarIoNotFound exception=assertThrows(
                UsuarIoNotFound.class,
                ()->service.crearDireccion(9,dtoAddressCreate)
        );

        assertEquals("User not found with id: 9",exception.getMessage());
        verify(repository,never()).save(any());
    }

    @Test
    @DisplayName("LN03 - Debe desactivar direcciones principales anteriores")
    void creacionDireccionDesactivaAnteriores() {
        Direcciones direccionAnterior = new Direcciones(
                2, usuario, "Colombia", "Bogotá", "Cundinamarca", "Calle 1", true
        );

        dtoAddressCreate.setPrincipal(true);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repository.findAllByUsuario_Id(1)).thenReturn(List.of(direccionAnterior));
        when(mapper.toDomain(any(DtoAddressCreate.class))).thenReturn(direccion);
        when(repository.save(any(Direcciones.class))).thenReturn(direccion);

        Direcciones resultado = service.crearDireccion(1, dtoAddressCreate);

        assertNotNull(resultado);
        assertEquals(true, resultado.isPrincipal());

        verify(repository, times(1)).removeAllPrincipalFlags(1);
        verify(repository, times(1)).save(any(Direcciones.class));
    }

    @Test
    @DisplayName("lN04 - admin error no puede tener direcciones")
    void creacionDireccionADminError(){
        Usuarios usuarioAdmin=new Usuarios("Admin", "admin@test.com", "Password@123!");
        usuarioAdmin.setId(2);
        usuarioAdmin.setTipo(UserType.administrador);

        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuarioAdmin));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.crearDireccion(2, dtoAddressCreate)
        );

        assertEquals("Admins cannot have addresses",exception.getMessage());
         verify(repository,never()).save(any());
    }

    @Test
    @DisplayName("LN05 - Debe rechazar cualquier dato vacío")
    void creacionDireccionConDatosVaciosError() {

        dtoAddressCreate.setPais("");  // País vacío

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Direcciones direccionInvalida = new Direcciones(
                1, usuario, "", "Colombia", "Antioquia", "av 33#54", true
        );
        when(mapper.toDomain(any(DtoAddressCreate.class))).thenReturn(direccionInvalida);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.crearDireccion(1, dtoAddressCreate)
        );

        assertEquals("City cannot be null or empty", exception.getMessage());
        verify(repository, never()).save(any());
    }
    @Test
    @DisplayName("LN06 - Primera dirección debe ser principal automáticamente")
    void creacionDireccionPrimeraDelUsuarioPrincipal() {

        dtoAddressCreate.setPrincipal(false);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repository.findAllByUsuario_Id(1)).thenReturn(Collections.emptyList());

        Direcciones direccionNoPrincipal = new Direcciones(
                1, usuario, "Colombia", "Medellin", "Antioquia", "av 33#54", false
        );
        when(mapper.toDomain(any(DtoAddressCreate.class))).thenReturn(direccionNoPrincipal);
        when(repository.save(any(Direcciones.class))).thenReturn(direccion);


        Direcciones resultado = service.crearDireccion(1, dtoAddressCreate);

        assertEquals(true, resultado.isPrincipal());
        verify(repository, times(1)).save(any(Direcciones.class));
    }
    // ════════════════════════════════════════════════════════════════════
// GRUPO 2: OBTENER DIRECCIONES
// ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN107 - Obtener direcciones de usuario exitosamente")
    void obtenerDireccionesUsuarioExiste() {
        // ARRANGE
        Direcciones direccion1 = new Direcciones(1, usuario, "Medellin", "Colombia", "Antioquia", "av 33#54", true);
        Direcciones direccion2 = new Direcciones(2, usuario, "Bogotá", "Colombia", "Cundinamarca", "Calle 1", false);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repository.findAllByUsuario_Id(1)).thenReturn(List.of(direccion1, direccion2));

        List<Direcciones> resultado = service.obtenerDirecciones(1);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findById(1);
        verify(repository, times(1)).findAllByUsuario_Id(1);
    }

    @Test
    @DisplayName("LN08 - Obtener direcciones: usuario no existe")
    void obtenerDireccionesUsuarioNoExiste() {

        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.obtenerDirecciones(999)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(repository, never()).findAllByUsuario_Id(anyInt());
    }

    // ════════════════════════════════════════════════════════════════════
// GRUPO 4: ACTUALIZAR DIRECCIÓN
// ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN09 - Actualizar dirección exitosamente")
    void actualizarDireccionExitosa() {

        Direcciones direccionExistente = new Direcciones(1, usuario, "Medellin", "Colombia", "Antioquia", "av 33#54", false);

        DtoAddressCreate dtoActualizado = new DtoAddressCreate("Colombia", "Bogotá", "Cundinamarca", "Calle 100", false);
        Direcciones direccionActualizada = new Direcciones(null, null, "Bogotá", "Colombia", "Cundinamarca", "Calle 100", false);

        when(repository.findById(1)).thenReturn(Optional.of(direccionExistente));
        when(mapper.toDomain(any(DtoAddressCreate.class))).thenReturn(direccionActualizada);
        when(repository.save(any(Direcciones.class))).thenReturn(direccionExistente);

        // ACT
        Direcciones resultado = service.actualizarDireccion(1, dtoActualizado);

        // ASSERT
        assertNotNull(resultado);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(any(Direcciones.class));
    }

    @Test
    @DisplayName("LN10 - Actualizar a principal desactiva otras direcciones principales")
    void actualizarDireccionAPrincipal() {

        Direcciones direccionExistente = new Direcciones(1, usuario, "Medellin", "Colombia", "Antioquia", "av 33#54", false);

        DtoAddressCreate dtoActualizado = new DtoAddressCreate("Colombia", "Medellin", "Antioquia", "av 33#54", true);
        Direcciones direccionActualizada = new Direcciones(null, null, "Medellin", "Colombia", "Antioquia", "av 33#54", true);

        when(repository.findById(1)).thenReturn(Optional.of(direccionExistente));
        when(mapper.toDomain(any(DtoAddressCreate.class))).thenReturn(direccionActualizada);
        when(repository.save(any(Direcciones.class))).thenReturn(direccionExistente);

        service.actualizarDireccion(1, dtoActualizado);

        verify(repository, times(1)).removeAllPrincipalFlags(usuario.getId());
        verify(repository, times(1)).save(any(Direcciones.class));
    }

    @Test
    @DisplayName("LN11 - Actualizar: dirección no existe")
    void actualizarDireccionNoExiste() {

        when(repository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarDireccion(999, dtoAddressCreate)
        );

        assertEquals("Address not found with id: 999", exception.getMessage());
        verify(repository, never()).save(any());
    }
    // ════════════════════════════════════════════════════════════════════
// GRUPO 5: ELIMINAR DIRECCIÓN
// ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN12 - Eliminar dirección no principal")
    void eliminarDireccion() {

        Direcciones direccionNoPrincipal = new Direcciones(2, usuario, "Bogotá", "Colombia", "Cundinamarca", "Calle 1", false);

        when(repository.findById(2)).thenReturn(Optional.of(direccionNoPrincipal));

        service.eliminarDireccion(2);

        verify(repository, times(1)).delete(direccionNoPrincipal);
        verify(repository, never()).save(any());  // No necesita promover otra
    }

    @Test
    @DisplayName("LN13 - Eliminar dirección principal promueve otra a principal")
    void eliminarDireccionprincipalPromueveOtra() {

        Direcciones direccionPrincipal = new Direcciones(1, usuario, "Medellin", "Colombia", "Antioquia", "av 33#54", true);
        Direcciones otraDireccion = new Direcciones(2, usuario, "Bogotá", "Colombia", "Cundinamarca", "Calle 1", false);

        when(repository.findById(1)).thenReturn(Optional.of(direccionPrincipal));
        when(repository.findAllByUsuario_Id(1)).thenReturn(List.of(otraDireccion));

        service.eliminarDireccion(1);

        verify(repository, times(1)).delete(direccionPrincipal);
        verify(repository, times(1)).save(otraDireccion);
    }

    @Test
    @DisplayName("LN14 - Eliminar última dirección del usuario")
    void eliminarDireccionultima() {
        Direcciones unicaDireccion = new Direcciones(1, usuario, "Medellin", "Colombia", "Antioquia", "av 33#54", true);

        when(repository.findById(1)).thenReturn(Optional.of(unicaDireccion));
        when(repository.findAllByUsuario_Id(1)).thenReturn(Collections.emptyList());
        service.eliminarDireccion(1);

        verify(repository, times(1)).delete(unicaDireccion);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN15 - Eliminar: dirección no existe")
    void eliminarDireccionNoExiste() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.eliminarDireccion(999)
        );

        assertEquals("Address not found with id: 999", exception.getMessage());
        verify(repository, never()).delete(any());
    }

}
