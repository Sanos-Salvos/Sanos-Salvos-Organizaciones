package com.sanosysalvos.organizaciones.repository;

import com.sanosysalvos.organizaciones.model.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {
    List<Organizacion> findByTipo(String tipo);
}