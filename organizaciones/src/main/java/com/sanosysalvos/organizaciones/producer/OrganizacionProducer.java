package com.sanosysalvos.organizaciones.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrganizacionProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void enviarEventoRegistro(String nombreOrganizacion) {
        String mensaje = "Nueva organización registrada: " + nombreOrganizacion;
        // Envía el mensaje al tópico de Kafka
        kafkaTemplate.send("organizaciones-topic", mensaje);
        System.out.println("Evento enviado a Kafka: " + mensaje);
    }
}