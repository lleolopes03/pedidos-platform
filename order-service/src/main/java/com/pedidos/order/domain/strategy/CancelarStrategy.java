package com.pedidos.order.domain.strategy;

import com.pedidos.order.domain.enums.StatusPedido;
import com.pedidos.order.exception.BusinessException;

public class CancelarStrategy implements StatusTransitionStrategy {
    @Override
    public void validar(StatusPedido statusAtual) {
        if (statusAtual == StatusPedido.ENTREGUE || statusAtual == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido nao pode ser cancelado no status: " + statusAtual);
        }
    }
}
