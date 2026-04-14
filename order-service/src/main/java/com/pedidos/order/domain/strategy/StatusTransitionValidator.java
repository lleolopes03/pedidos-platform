package com.pedidos.order.domain.strategy;

import com.pedidos.order.domain.enums.StatusPedido;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class StatusTransitionValidator {

    private final Map<StatusPedido, StatusTransitionStrategy> strategies = Map.of(
        StatusPedido.CONFIRMADO, new ConfirmarStrategy(),
        StatusPedido.ENVIADO, new EnviarStrategy(),
        StatusPedido.ENTREGUE, new EntregarStrategy(),
        StatusPedido.CANCELADO, new CancelarStrategy()
    );

    public void validar(StatusPedido novoStatus, StatusPedido statusAtual) {
        StatusTransitionStrategy strategy = strategies.get(novoStatus);
        if (strategy != null) {
            strategy.validar(statusAtual);
        }
    }
}
