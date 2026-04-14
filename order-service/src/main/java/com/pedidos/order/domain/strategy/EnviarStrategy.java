package com.pedidos.order.domain.strategy;

import com.pedidos.order.domain.enums.StatusPedido;
import com.pedidos.order.exception.BusinessException;

public class EnviarStrategy implements StatusTransitionStrategy {
    @Override
    public void validar(StatusPedido statusAtual) {
        if (statusAtual != StatusPedido.CONFIRMADO) {
            throw new BusinessException("Pedido so pode ser enviado quando CONFIRMADO. Status atual: " + statusAtual);
        }
    }
}
