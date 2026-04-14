package com.pedidos.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponse {
    private Long id;
    private String clienteNome;
    private String clienteEmail;
    private String status;
    private BigDecimal total;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoResponse> itens;
}
