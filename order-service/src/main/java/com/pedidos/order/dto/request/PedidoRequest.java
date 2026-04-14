package com.pedidos.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequest {
    @NotBlank(message = "Nome do cliente e obrigatorio")
    private String clienteNome;

    @NotBlank(message = "Email do cliente e obrigatorio")
    @Email(message = "Email invalido")
    private String clienteEmail;

    @NotEmpty(message = "Pedido deve ter ao menos um item")
    @Valid
    private List<ItemPedidoRequest> itens;
}
