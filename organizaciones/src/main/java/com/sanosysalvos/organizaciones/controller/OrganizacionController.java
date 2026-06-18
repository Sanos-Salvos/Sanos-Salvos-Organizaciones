package com.sanosysalvos.organizaciones.controller;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.service.IOrganizacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones")
@CrossOrigin(origins = "*")
public class OrganizacionController {

    private final IOrganizacionService service;

    public OrganizacionController(IOrganizacionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrganizacionDTO> registrarOrganizacion(@Valid @RequestBody OrganizacionDTO dto) {
        OrganizacionDTO registrada = service.guardar(dto);
        return new ResponseEntity<>(registrada, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrganizacionDTO>> listarTodas() {
        List<OrganizacionDTO> lista = service.obtenerTodas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizacionDTO> buscarPorId(@PathVariable Long id) {
        OrganizacionDTO org = service.obtenerPorId(id);
        return ResponseEntity.ok(org);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizacionDTO> actualizarOrganizacion(@PathVariable Long id, @Valid @RequestBody OrganizacionDTO dto) {
        OrganizacionDTO actualizada = service.actualizar(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarOrganizacion(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok("Organización eliminada correctamente");
    }
}