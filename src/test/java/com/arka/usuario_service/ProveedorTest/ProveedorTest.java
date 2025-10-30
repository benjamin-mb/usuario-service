package com.arka.usuario_service.ProveedorTest;

import com.arka.usuario_service.DTO.ProovedorDto;
import com.arka.usuario_service.Mapper.ProovedorMapper;
import com.arka.usuario_service.excepcion.ProveedorNotFoundException;
import com.arka.usuario_service.model.Proovedores;
import com.arka.usuario_service.repositorio.ProovedorRepository;
import com.arka.usuario_service.service.ProveedoresService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("test proveedores")
public class ProveedorTest {

    @Mock
    private ProovedorRepository repository;

    @Mock
    private ProovedorMapper mapper;

    @InjectMocks
    private ProveedoresService service;

    private ProovedorDto proveedorDto;
    private Proovedores proveedor;

    @BeforeEach
    void setup(){
        proveedorDto=new ProovedorDto("AMERICANA S.A.S","320-789-069","encargado de repuestos");
        proveedor=new Proovedores("AMERICANA S.A.S","320-789-069","encargado de repuestos");
        proveedor.setId(1);
    }
    // ════════════════════════════════════════════════════════════════════
    // GRUPO 1: CREAR PROVEEDOR
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN01 - Create: debe crear proveedor exitosamente")
    void testCreate_casoExitoso() {
        /*
         * REGLA: Cuando todos los datos son válidos, debe guardarse
         *
         * FLUJO:
         * 1. Verificar que el nombre no existe
         * 2. Mapear DTO a entidad
         * 3. Guardar en BD
         * 4. Devolver el proveedor guardado
         */

        // ARRANGE: Configurar los mocks

        // 1. El nombre NO existe aún
        when(repository.existsByNombre(anyString())).thenReturn(false);

        // 2. Mapper convierte DTO a entidad
        when(mapper.toEntity(any(ProovedorDto.class))).thenReturn(proveedor);

        // 3. Repository guarda y devuelve el proveedor
        when(repository.save(any(Proovedores.class))).thenReturn(proveedor);

        // ACT: Llamar al método del servicio
        Proovedores resultado = service.create(proveedorDto);

        // ASSERT: Verificar que todo funcionó
        assertNotNull(resultado);
        assertEquals("AMERICANA S.A.S", resultado.getNombre());
        assertEquals(1, resultado.getId());

        // VERIFY: Verificar que se llamaron los métodos correctos
        verify(repository, times(1)).existsByNombre("AMERICANA S.A.S");
        verify(mapper, times(1)).toEntity(any(ProovedorDto.class));
        verify(repository, times(1)).save(any(Proovedores.class));
    }

    @Test
    @DisplayName("LN02 - Create: debe rechazar nombre vacío")
    void testCreate_nombreVacio_lanzaExcepcion() {
        /*
         * REGLA: El nombre no puede estar vacío
         * VALIDACIÓN: if (proovedor.getNombre().isBlank())
         */

        // ARRANGE: Nombre vacío
        proveedorDto.setNombre("");

        // ACT & ASSERT: Debe lanzar excepción
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("nombre can not be blank", exception.getMessage());

        // VERIFY: NO debe intentar guardar
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN03 - Create: debe rechazar nombre con solo espacios")
    void testCreate_nombreConEspacios_lanzaExcepcion() {
        /*
         * REGLA: El nombre no puede ser solo espacios en blanco
         * isBlank() detecta strings con solo espacios
         */

        // ARRANGE: Nombre con solo espacios
        proveedorDto.setNombre("   ");

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("nombre can not be blank", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN04 - Create: debe rechazar nombre null")
    void testCreate_nombreNull_lanzaExcepcion() {
        /*
         * REGLA: El nombre no puede ser null
         */

        // ARRANGE: Nombre null
        proveedorDto.setNombre(null);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("nombre can not be blank", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN05 - Create: debe rechazar nombre duplicado")
    void testCreate_nombreDuplicado_lanzaExcepcion() {
        /*
         * REGLA: No se permiten proveedores con el mismo nombre
         * VALIDACIÓN: if (repository.existsByNombre(...))
         */

        // ARRANGE: El nombre YA existe
        when(repository.existsByNombre(anyString())).thenReturn(true);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("proovedor already registered with that name", exception.getMessage());

        // VERIFY: Verificó existencia pero NO guardó
        verify(repository, times(1)).existsByNombre("AMERICANA S.A.S");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN06 - Create: debe trimear el nombre antes de validar")
    void testCreate_nombreConEspacios_debeTrimear() {
        /*
         * REGLA: El nombre debe trimearse antes de verificar duplicados
         * CÓDIGO: repository.existsByNombre(proovedor.getNombre().trim())
         */

        // ARRANGE: Nombre con espacios al inicio y final
        proveedorDto.setNombre("  AMERICANA S.A.S  ");

        when(repository.existsByNombre("AMERICANA S.A.S")).thenReturn(false);
        when(mapper.toEntity(any(ProovedorDto.class))).thenReturn(proveedor);
        when(repository.save(any(Proovedores.class))).thenReturn(proveedor);

        // ACT
        service.create(proveedorDto);

        // ASSERT: Debe verificar SIN espacios
        verify(repository, times(1)).existsByNombre("AMERICANA S.A.S");
    }

    @Test
    @DisplayName("LN07 - Create: debe rechazar teléfono vacío")
    void testCreate_telefonoVacio_lanzaExcepcion() {
        /*
         * REGLA: El teléfono no puede estar vacío
         */

        // ARRANGE
        proveedorDto.setTelefono("");
        when(repository.existsByNombre(anyString())).thenReturn(false);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("phone number can not be blank or contains less than 10 didgits",
                exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN08 - Create: debe rechazar teléfono menor a 8 dígitos")
    void testCreate_telefonoMenorA8Digitos_lanzaExcepcion() {
        /*
         * REGLA: El teléfono debe tener mínimo 8 dígitos
         * VALIDACIÓN: if (proovedor.getTelefono().length() < 8)
         */

        // ARRANGE: Teléfono con solo 7 dígitos
        proveedorDto.setTelefono("3201234");  // 7 dígitos
        when(repository.existsByNombre(anyString())).thenReturn(false);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("phone number can not be blank or contains less than 10 didgits",
                exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("LN09 - Create: debe aceptar teléfono con exactamente 8 dígitos")
    void testCreate_telefonoCon8Digitos_esValido() {
        /*
         * REGLA: Teléfono con 8 dígitos es válido (mínimo)
         */

        // ARRANGE: Exactamente 8 dígitos
        proveedorDto.setTelefono("12345678890");

        when(repository.existsByNombre(anyString())).thenReturn(false);
        when(mapper.toEntity(any(ProovedorDto.class))).thenReturn(proveedor);
        when(repository.save(any(Proovedores.class))).thenReturn(proveedor);

        // ACT & ASSERT: No debe lanzar excepción
        assertDoesNotThrow(() -> service.create(proveedorDto));

        verify(repository, times(1)).save(any(Proovedores.class));
    }

    @Test
    @DisplayName("LN10 - Create: debe rechazar características vacías")
    void testCreate_caracteristicasVacias_lanzaExcepcion() {
        /*
         * REGLA: Las características no pueden estar vacías
         */

        // ARRANGE
        proveedorDto.setCaracteristicas("");
        when(repository.existsByNombre(anyString())).thenReturn(false);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(proveedorDto)
        );

        assertEquals("caracteristicas can not be blank", exception.getMessage());
        verify(repository, never()).save(any());
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 2: BUSCAR PROVEEDOR
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN11 - FindById: debe encontrar proveedor por ID")
    void testFindById_proveedorExiste_loDevuelve() {
        /*
         * REGLA: Buscar proveedor por ID debe devolverlo si existe
         */

        // ARRANGE
        when(repository.findById(1)).thenReturn(Optional.of(proveedor));

        // ACT
        Proovedores resultado = service.findById(1);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("AMERICANA S.A.S", resultado.getNombre());
        assertEquals(1, resultado.getId());

        verify(repository, times(1)).findById(1);
    }

    @Test
    @DisplayName("LN12 - FindById: debe lanzar excepción si no existe")
    void testFindById_proveedorNoExiste_lanzaExcepcion() {
        /*
         * REGLA: Si el proveedor no existe, lanzar ProveedorNotFoundException
         */

        // ARRANGE: El proveedor NO existe
        when(repository.findById(999)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ProveedorNotFoundException exception = assertThrows(
                ProveedorNotFoundException.class,
                () -> service.findById(999)
        );

        assertEquals("user not founf by id:999", exception.getMessage());
    }

    // ════════════════════════════════════════════════════════════════════
    // GRUPO 3: ACTUALIZAR PROVEEDOR
    // ════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("LN13 - Update: debe actualizar solo el nombre")
    void testUpdate_soloNombre_funciona() {
        /*
         * REGLA: Se puede actualizar solo el nombre
         * LÓGICA: if (!(proovedor.getNombre() == null))
         */

        // ARRANGE
        Proovedores proveedorActualizado = new Proovedores(
                "PANAMERICANA S.A.S",  // Nuevo nombre
                null,  // Teléfono null (no actualiza)
                null   // Características null (no actualiza)
        );
        proveedorActualizado.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(proveedor));
        when(repository.save(any(Proovedores.class))).thenReturn(proveedor);

        // ACT
        Proovedores resultado = service.updateProovedor(proveedorActualizado);

        // ASSERT
        assertNotNull(resultado);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(proveedor);
    }

    @Test
    @DisplayName("LN14 - Update: debe actualizar solo el teléfono")
    void testUpdate_soloTelefono_funciona() {
        /*
         * REGLA: Se puede actualizar solo el teléfono
         */

        // ARRANGE
        Proovedores proveedorActualizado = new Proovedores(
                null,           // Nombre null
                "3119876543",   // Nuevo teléfono
                null            // Características null
        );
        proveedorActualizado.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(proveedor));
        when(repository.save(any(Proovedores.class))).thenReturn(proveedor);

        // ACT
        service.updateProovedor(proveedorActualizado);

        // ASSERT
        verify(repository, times(1)).save(proveedor);
    }

    @Test
    @DisplayName("LN15 - Update: debe actualizar todos los campos")
    void testUpdate_todosCampos_funciona() {
        /*
         * REGLA: Se pueden actualizar todos los campos a la vez
         */

        // ARRANGE
        Proovedores proveedorActualizado = new Proovedores(
                "PANAMERICANA S.A.S",
                "3119876543",
                "Nuevas características"
        );
        proveedorActualizado.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(proveedor));
        when(repository.save(any(Proovedores.class))).thenReturn(proveedor);

        // ACT
        service.updateProovedor(proveedorActualizado);

        // ASSERT
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(proveedor);
    }

    @Test
    @DisplayName("LN16 - Update: debe lanzar excepción si no existe")
    void testUpdate_proveedorNoExiste_lanzaExcepcion() {
        /*
         * REGLA: No se puede actualizar un proveedor que no existe
         */

        // ARRANGE
        Proovedores proveedorActualizado = new Proovedores(
                "PANAMERICANA",
                "3119999999",
                "Nuevas características"
        );
        proveedorActualizado.setId(999);

        when(repository.findById(999)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ProveedorNotFoundException exception = assertThrows(
                ProveedorNotFoundException.class,
                () -> service.updateProovedor(proveedorActualizado)
        );

        assertEquals("producto not found with id:999", exception.getMessage());
        verify(repository, never()).save(any());
    }

}
