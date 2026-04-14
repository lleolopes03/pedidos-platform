package com.pedidos.order.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEvent {
    private Long pedidoId;
    private String clienteEmail;
    private String clienteNome;
    private String status;
    private BigDecimal total;
}
