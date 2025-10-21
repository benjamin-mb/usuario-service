package com.arka.usuario_service.controller;

import com.arka.usuario_service.DTO.DtoAddressCreate;
import com.arka.usuario_service.model.Direcciones;
import com.arka.usuario_service.service.DireccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/direcciones")
public class DireccionController {

    private final DireccionService service;

    public DireccionController(DireccionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Direcciones> crear(@PathVariable Integer idUsuario,@RequestBody DtoAddressCreate dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crearDireccion(idUsuario, dto));
    }

    @GetMapping
    public ResponseEntity<List<Direcciones>> listar(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(service.obtenerDirecciones(idUsuario));
    }

    @GetMapping("/principal")
    public ResponseEntity<Direcciones> obtenerPrincipal(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(service.obtenerDireccionPrincipal(idUsuario));
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<Direcciones> actualizar(
            @PathVariable Integer idDireccion,
            @RequestBody DtoAddressCreate dto) {
        return ResponseEntity.ok(service.actualizarDireccion(idDireccion, dto));
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idDireccion) {
        service.eliminarDireccion(idDireccion);
        return ResponseEntity.noContent().build();
    }
}
