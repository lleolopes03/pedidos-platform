package com.pedidos.order.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemPedidoRequest {
    @NotNull(message = "ProdutoId e obrigatorio")
    private Long produtoId;

    @NotNull(message = "Quantidade e obrigatoria")
    @Min(value = 1, message = "Quantidade deve ser ao menos 1")
    private Integer quantidade;
}
