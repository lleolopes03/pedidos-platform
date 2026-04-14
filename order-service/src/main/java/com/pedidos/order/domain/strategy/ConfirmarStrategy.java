package com.pedidos.order.domain.strategy;

import com.pedidos.order.domain.enums.StatusPedido;
import com.pedidos.order.exception.BusinessException;

public class ConfirmarStrategy implements StatusTransitionStrategy {
    @Override
    public void validar(StatusPedido statusAtual) {
        if (statusAtual != StatusPedido.PENDENTE) {
            throw new BusinessException("Pedido so pode ser confirmado quando PENDENTE. Status atual: " + statusAtual);
        }
    }
}
