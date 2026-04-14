package com.pedidos.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemPedidoResponse {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}
