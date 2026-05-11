package com.sanosysalvos.organizaciones.controller;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.model.Organizacion;
import com.sanosysalvos.organizaciones.service.OrganizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones")
public class OrganizacionController {

    @Autowired
    private OrganizacionService service;

    @PostMapping("/crear")
    public ResponseEntity<Organizacion> registrarOrganizacion(@RequestBody OrganizacionDTO dto) {
        Organizacion registrada = service.registrar(dto);

        if (registrada.getId() != null && registrada.getId() == -1L) {
            return new ResponseEntity<>(registrada, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(registrada, HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Organizacion>> listarTodas() {
        List<Organizacion> lista = service.obtenerTodas();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Organizacion> buscarPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(org -> {
                    if (org.getId() != null && org.getId() == -1L) {
                        return new ResponseEntity<>(org, HttpStatus.SERVICE_UNAVAILABLE);
                    }
                    return new ResponseEntity<>(org, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarOrganizacion(@PathVariable Long id, @RequestBody OrganizacionDTO dto) {
        try {
            Organizacion actualizada = service.actualizar(id, dto);
            if (actualizada.getId() != null && actualizada.getId() == -1L) {
                return new ResponseEntity<>(actualizada, HttpStatus.SERVICE_UNAVAILABLE);
            }
            return new ResponseEntity<>(actualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarOrganizacion(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return new ResponseEntity<>("Solicitud de eliminación procesada", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}