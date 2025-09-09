package com.arka.usuario_service.controller;

import com.arka.usuario_service.DTO.ProovedorDto;
import com.arka.usuario_service.model.Proovedores;
import com.arka.usuario_service.service.ProveedoresService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProovedorController {

    private final ProveedoresService proveedoresService;

    public ProovedorController(ProveedoresService proveedoresService) {
        this.proveedoresService = proveedoresService;
    }

    // Crear proveedor
    @PostMapping
    public ResponseEntity<Proovedores> createProveedor(@RequestBody ProovedorDto proovedorDto) {
        Proovedores nuevoProveedor = proveedoresService.create(proovedorDto);
        return ResponseEntity.ok(nuevoProveedor);
    }

    // Obtener todos los proveedores
    @GetMapping
    public ResponseEntity<List<Proovedores>> getAllProveedores() {
        return ResponseEntity.ok(proveedoresService.findAll());
    }

    // Obtener proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proovedores> getProveedorById(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedoresService.findById(id));
    }

    // Actualizar proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Proovedores> updateProveedor(@PathVariable Integer id, @RequestBody Proovedores proovedor) {
        // Aseguramos que el ID recibido coincida con el del objeto
        proovedor.setId(id);
        return ResponseEntity.ok(proveedoresService.updateProovedor(proovedor));
    }
}

