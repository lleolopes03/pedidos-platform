package com.pedidos.notification.kafka;

import com.pedidos.notification.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEventConsumer {

    private final NotificacaoService notificacaoService;

    @KafkaListener(topics = "pedido-status", groupId = "notification-group")
    public void consumir(PedidoEvent event) {
        log.info("Evento recebido: pedidoId={} status={}", event.getPedidoId(), event.getStatus());
        notificacaoService.processarEvento(event);
    }
}
