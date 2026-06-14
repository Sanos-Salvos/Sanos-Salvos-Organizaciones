package com.sanosysalvos.organizaciones.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrganizacionProducer {

    private static final Logger log = LoggerFactory.getLogger(OrganizacionProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "organizaciones-topic";

    public OrganizacionProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarEventoRegistro(String mensaje) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, mensaje);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento enviado a Kafka exitosamente en el topic [{}] con offset [{}]",
                        TOPIC, result.getRecordMetadata().offset());
            } else {
                log.error("Error al enviar evento a Kafka en el topic [{}]. Mensaje: {}",
                        TOPIC, ex.getMessage());
            }
        });
    }
}