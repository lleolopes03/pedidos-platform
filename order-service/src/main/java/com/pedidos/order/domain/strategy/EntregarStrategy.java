package com.pedidos.order.domain.strategy;

import com.pedidos.order.domain.enums.StatusPedido;
import com.pedidos.order.exception.BusinessException;

public class EntregarStrategy implements StatusTransitionStrategy {
    @Override
    public void validar(StatusPedido statusAtual) {
        if (statusAtual != StatusPedido.ENVIADO) {
            throw new BusinessException("Pedido so pode ser entregue quando ENVIADO. Status atual: " + statusAtual);
        }
    }
}
