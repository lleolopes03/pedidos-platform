package com.pedidos.order.domain.strategy;

import com.pedidos.order.domain.enums.StatusPedido;

public interface StatusTransitionStrategy {
    void validar(StatusPedido statusAtual);
}
