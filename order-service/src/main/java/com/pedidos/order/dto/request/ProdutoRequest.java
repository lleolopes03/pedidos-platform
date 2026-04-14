package com.pedidos.order.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoRequest {
    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @NotNull(message = "Preco e obrigatorio")
    @DecimalMin(value = "0.01", message = "Preco deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "Estoque e obrigatorio")
    @Min(value = 0, message = "Estoque nao pode ser negativo")
    private Integer estoque;
}
