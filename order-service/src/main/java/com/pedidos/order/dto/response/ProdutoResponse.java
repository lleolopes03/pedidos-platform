package com.pedidos.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoResponse {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer estoque;
}
