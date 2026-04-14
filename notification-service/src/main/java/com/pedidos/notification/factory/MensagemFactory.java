package com.pedidos.notification.factory;

import com.pedidos.notification.kafka.PedidoEvent;
import org.springframework.stereotype.Component;

@Component
public class MensagemFactory {

    public String criar(PedidoEvent event) {
        return switch (event.getStatus()) {
            case "PENDENTE" -> String.format("Ola %s, seu pedido #%d foi recebido e esta aguardando confirmacao. Total: R$ %.2f",
                event.getClienteNome(), event.getPedidoId(), event.getTotal());
            case "CONFIRMADO" -> String.format("Ola %s, seu pedido #%d foi confirmado e esta sendo preparado!",
                event.getClienteNome(), event.getPedidoId());
            case "ENVIADO" -> String.format("Ola %s, seu pedido #%d foi enviado e esta a caminho!",
                event.getClienteNome(), event.getPedidoId());
            case "ENTREGUE" -> String.format("Ola %s, seu pedido #%d foi entregue. Obrigado pela compra!",
                event.getClienteNome(), event.getPedidoId());
            case "CANCELADO" -> String.format("Ola %s, seu pedido #%d foi cancelado. Entre em contato se precisar de ajuda.",
                event.getClienteNome(), event.getPedidoId());
            default -> String.format("Atualizacao do pedido #%d: status %s", event.getPedidoId(), event.getStatus());
        };
    }
}
