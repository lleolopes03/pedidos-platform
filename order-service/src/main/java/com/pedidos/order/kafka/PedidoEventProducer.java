package com.pedidos.order.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEventProducer {

    private static final String TOPIC = "pedido-status";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publicar(PedidoEvent event) {
        log.info("Publicando evento Kafka: pedidoId={} status={}", event.getPedidoId(), event.getStatus());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getPedidoId()), event);
    }
}
