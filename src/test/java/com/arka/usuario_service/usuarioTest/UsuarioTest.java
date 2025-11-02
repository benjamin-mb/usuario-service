
package com.arka.usuario_service.usuarioTest;
// IMPORTANTE: Estos son los imports para any(), anyString(), etc.
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import com.arka.usuario_service.DTO.AdminDto;
import com.arka.usuario_service.DTO.UserDto;
import com.arka.usuario_service.DTO.UserResponse;
import com.arka.usuario_service.DTO.UserUpdateEmailOrName;
import com.arka.usuario_service.Mapper.UserMapper;
import com.arka.usuario_service.excepcion.UsuarIoNotFound;
import com.arka.usuario_service.model.UserType;
import com.arka.usuario_service.model.Usuarios;
import com.arka.usuario_service.repositorio.UsuarioRepository;
import com.arka.usuario_service.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("test de usuaroio logica")
public class UsuarioTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private UserDto userDto;
    private Usuarios usuario;
    private UserResponse userResponse;

    @BeforeEach
    void setup(){
        userDto=new UserDto("Benjamin","benjamin0@gmail.com","Password@123!");
        usuario=new Usuarios("Benjamin","benjamin0@gmail.com","paswwordEncoder1");
        userResponse=new UserResponse("Benjamin","benjamin0@gmail.com");
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 1: VALIDACIÓN DE PASSWORD (Método validatePassword)
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN01 - ValidatePassword: debe rechazar password menor a 8 caracteres")
    void testValidatePassword_menorA8Caracteres_lanzaExcepcion() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePassword("Pass@1")
        );

        assertEquals("Password too short", exception.getMessage());
    }

    @Test
    @DisplayName("LN02 - ValidatePassword: debe rechazar password sin mayúscula")
    void testValidatePassword_sinMayuscula_lanzaExcepcion() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePassword("password@123!")
        );

        assertEquals("Password must have an uppercase letter and a special character",
                exception.getMessage());
    }

    @Test
    @DisplayName("LN03 - ValidatePassword: debe rechazar password sin carácter especial")
    void testValidatePassword_sinCaracterEspecial_lanzaExcepcion() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePassword("Password123")
        );

        assertEquals("Password must have an uppercase letter and a special character",
                exception.getMessage());
    }

    @Test
    @DisplayName("LN04 - ValidatePassword: debe aceptar password válida")
    void testValidatePassword_passwordValida_noLanzaExcepcion() {
        assertDoesNotThrow(() -> service.validatePassword("Password123!"));
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 2: CREAR USUARIO
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN05 - Create: debe rechazar email duplicado")
    void testCreate_emailDuplicado_lanzaExcepcion() {

        when(repository.existsByEmail("benjamin0@gmail.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(userDto)
        );

        assertEquals("Email is already registered", exception.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN06 - Create: debe trimear el email antes de validar")
    void testCreate_emailConEspacios_debeTrimear() {

        userDto.setEmail("  benjamin0@gmail.com  ");

        when(repository.existsByEmail("benjamin0@gmail.com")).thenReturn(false);
        when(mapper.toEntity(any(UserDto.class))).thenReturn(usuario);
        when(passwordEncoder.encode(any())).thenReturn("encrypted");
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(userResponse);

        service.create(userDto);

        assertEquals("benjamin0@gmail.com", userDto.getEmail());

        verify(repository).existsByEmail("benjamin0@gmail.com");
    }

    @Test
    @DisplayName("LN07 - Create: debe encriptar la password antes de guardar")
    void testCreate_debeEncriptarPassword() {
        when(repository.existsByEmail(any())).thenReturn(false);
        when(mapper.toEntity(any(UserDto.class))).thenReturn(usuario);
        when(passwordEncoder.encode("Password123!")).thenReturn("dfdnfiJBJ783433##");
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(userResponse);

        service.create(userDto);

        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("LN08 - Create: flujo completo exitoso")
    void testCreate_casoExitoso_guardaCorrectamente() {

        when(repository.existsByEmail("benjamin0@gmail.com")).thenReturn(false);
        when(mapper.toEntity(any(UserDto.class))).thenReturn(usuario);
        when(passwordEncoder.encode(any())).thenReturn("encrypted");
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(userResponse);

        UserResponse resultado = service.create(userDto);

        assertNotNull(resultado);
        assertEquals("Benjamin", resultado.getNombre());
        assertEquals("benjamin0@gmail.com", resultado.getEmail());

        verify(repository, times(1)).existsByEmail("benjamin0@gmail.com");
        verify(passwordEncoder, times(1)).encode("Password123!");
        verify(repository, times(1)).save(any());
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 3: CREAR ADMINISTRADOR
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN09 - CreateAdmin: debe crear con tipo administrador")
    void testCreateAdmin_debeAsignarTipoAdmin() {
        AdminDto adminDto = new AdminDto("Admin", "admin@test.com", "Admin@123");

        Usuarios adminUsuario = new Usuarios(
                "Admin",
                "admin@test.com",
                "encrypted",
                UserType.administrador
        );
        adminUsuario.setId(1);

        when(repository.existsByEmail(any())).thenReturn(false);
        when(mapper.toEntity(any(AdminDto.class))).thenReturn(adminUsuario);
        when(passwordEncoder.encode(any())).thenReturn("encrypted");
        when(repository.save(any())).thenReturn(adminUsuario);
        when(mapper.ToResponse(any())).thenReturn(new UserResponse("Admin", "admin@test.com"));

        UserResponse resultado = service.createAdmin(adminDto);

        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).encode("Admin@123");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("LN10 - CreateAdmin: debe validar email único")
    void testCreateAdmin_emailDuplicado_lanzaExcepcion() {

        AdminDto adminDto = new AdminDto("Admin", "admin@test.com", "Admin@123");
        when(repository.existsByEmail("admin@test.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createAdmin(adminDto)
        );

        assertEquals("Email is already registered", exception.getMessage());
        verify(repository, never()).save(any());
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 4: ACTUALIZAR PASSWORD
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN11 - UpdatePassword: debe buscar usuario por email")
    void testUpdatePassword_buscaUsuarioPorEmail() {

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(any())).thenReturn("newEncrypted");
        when(repository.save(any())).thenReturn(usuario);

        service.updatePassword("benjamin0@gmail.com", "NewPassword@123");

        verify(repository, times(1)).findByEmail("benjamin0@gmail.com");
        verify(passwordEncoder, times(1)).encode("NewPassword@123");
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("LN12 - UpdatePassword: debe validar la nueva password")
    void testUpdatePassword_passwordInvalida_lanzaExcepcion() {
        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updatePassword("benjamin0@gmail.com", "Pass@1")
        );

        assertEquals("Password too short", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN13 - UpdatePassword: debe encriptar la nueva password")
    void testUpdatePassword_debeEncriptarNuevaPassword() {

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("NewPassword@456")).thenReturn("$2a$10$newHash");
        when(repository.save(any())).thenReturn(usuario);

        service.updatePassword("benjamin0@gmail.com", "NewPassword@456");

        verify(passwordEncoder, times(1)).encode("NewPassword@456");
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("LN14 - UpdatePassword: usuario no existe, lanzar excepción")
    void testUpdatePassword_usuarioNoExiste_lanzaExcepcion() {

        when(repository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        UsuarIoNotFound exception = assertThrows(
                UsuarIoNotFound.class,
                () -> service.updatePassword("noexiste@test.com", "NewPassword@123")
        );

        assertEquals("User not found", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN15 - UpdatePassword: debe trimear el email")
    void testUpdatePassword_emailConEspacios_debeTrimear() {

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(any())).thenReturn("encrypted");
        when(repository.save(any())).thenReturn(usuario);

        service.updatePassword("  benjamin0@gmail.com  ", "NewPassword@123");

        verify(repository, times(1)).findByEmail("benjamin0@gmail.com");
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 5: ACTUALIZAR USUARIO (nombre/email)
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN16 - UpdateUser: debe permitir cambiar solo el nombre")
    void testUpdateUser_soloNombre_funciona() {

        UserUpdateEmailOrName request = new UserUpdateEmailOrName();
        request.setNombre("Juan Carlos Perez");

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(
                new UserResponse("Juan Carlos Perez", "benjamin0@gmail.com")
        );

        UserResponse resultado = service.updateUser("benjamin0@gmail.com", request);

        assertNotNull(resultado);
        assertEquals("Juan Carlos Perez", resultado.getNombre());
        verify(repository, times(1)).save(usuario);
        verify(repository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("LN17 - UpdateUser: debe permitir cambiar solo el email")
    void testUpdateUser_soloEmail_funciona() {

        UserUpdateEmailOrName request = new UserUpdateEmailOrName();
        request.setEmail("juannuevo@test.com");

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(repository.existsByEmail("juannuevo@test.com")).thenReturn(false);
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(
                new UserResponse("Benjamin", "juannuevo@test.com")
        );

        UserResponse resultado = service.updateUser("benjamin0@gmail.com", request);

        assertNotNull(resultado);
        verify(repository, times(1)).existsByEmail("juannuevo@test.com");
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("LN18 - UpdateUser: debe rechazar email duplicado")
    void testUpdateUser_emailDuplicado_lanzaExcepcion() {
        UserUpdateEmailOrName request = new UserUpdateEmailOrName();
        request.setEmail("existente@test.com");

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(repository.existsByEmail("existente@test.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateUser("benjamin0@gmail.com", request)
        );

        assertEquals("Email is already registered", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN19 - UpdateUser: debe permitir actualizar con mismo email")
    void testUpdateUser_mismoEmail_funciona() {

        UserUpdateEmailOrName request = new UserUpdateEmailOrName();
        request.setNombre("Juan Carlos");
        request.setEmail("benjamin0@gmail.com");  // Mismo email

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(repository.existsByEmail("benjamin0@gmail.com")).thenReturn(true);
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(
                new UserResponse("Juan Carlos", "benjamin0@gmail.com")
        );

        UserResponse resultado = service.updateUser("benjamin0@gmail.com", request);

        assertNotNull(resultado);
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("LN20 - UpdateUser: cambiar nombre y email simultáneamente")
    void testUpdateUser_ambos_funciona() {

        UserUpdateEmailOrName request = new UserUpdateEmailOrName();
        request.setNombre("Juan Carlos");
        request.setEmail("juannuevo@test.com");

        when(repository.findByEmail("benjamin0@gmail.com")).thenReturn(Optional.of(usuario));
        when(repository.existsByEmail("juannuevo@test.com")).thenReturn(false);
        when(repository.save(any())).thenReturn(usuario);
        when(mapper.ToResponse(any())).thenReturn(
                new UserResponse("Juan Carlos", "juannuevo@test.com")
        );

        UserResponse resultado = service.updateUser("benjamin0@gmail.com", request);

        assertNotNull(resultado);
        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals("juannuevo@test.com", resultado.getEmail());
        verify(repository, times(1)).existsByEmail("juannuevo@test.com");
        verify(repository, times(1)).save(usuario);
    }
}

