package com.sanosysalvos.organizaciones.controller;

import com.sanosysalvos.organizaciones.OrganizacionesApplication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.datasource.url=jdbc:h2:mem:orgdb;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
class OrganizacionesApplicationTest {

    @Test
    void contextLoads() {
        // Pasa automáticamente al simular las propiedades en memoria
    }

    @Test
    void mainMethodTest() {
        // Forzamos al método main real a usar el perfil de test para cubrir el 100% de la clase principal
        OrganizacionesApplication.main(new String[] {"--spring.profiles.active=test"});
    }
}