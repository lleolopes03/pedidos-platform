package com.pedidos.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacaoResponse {
    private Long id;
    private Long pedidoId;
    private String clienteEmail;
    private String mensagem;
    private String status;
    private LocalDateTime dataEnvio;
}
