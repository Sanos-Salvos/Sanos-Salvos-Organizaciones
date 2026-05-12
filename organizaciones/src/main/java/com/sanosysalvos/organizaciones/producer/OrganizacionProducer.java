package com.sanosysalvos.organizaciones.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrganizacionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrganizacionProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarEventoRegistro(String mensaje) {
        kafkaTemplate.send("organizaciones-topic", mensaje);
        System.out.println("Evento enviado a Kafka: " + mensaje);
    }
}