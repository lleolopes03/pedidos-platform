package com.pedidos.notification.controller;

import com.pedidos.notification.dto.NotificacaoResponse;
import com.pedidos.notification.dto.mapper.NotificacaoMapper;
import com.pedidos.notification.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notificacoes")
@RequiredArgsConstructor
@Tag(name = "Notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final NotificacaoMapper notificacaoMapper;

    @GetMapping
    @Operation(summary = "Listar todas as notificacoes")
    public ResponseEntity<List<NotificacaoResponse>> listarTodas() {
        return ResponseEntity.ok(notificacaoService.listarTodas().stream()
            .map(notificacaoMapper::toResponse).toList());
    }

    @GetMapping("/{pedidoId}")
    @Operation(summary = "Listar notificacoes por pedido")
    public ResponseEntity<List<NotificacaoResponse>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(notificacaoService.listarPorPedido(pedidoId).stream()
            .map(notificacaoMapper::toResponse).toList());
    }
}
