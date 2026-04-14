package com.pedidos.order.controller;

import com.pedidos.order.dto.request.PedidoRequest;
import com.pedidos.order.dto.response.PedidoResponse;
import com.pedidos.order.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Criar pedido")
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos")
    public ResponseEntity<List<PedidoResponse>> listar() {
        return ResponseEntity.ok(pedidoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar pedido")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.confirmar(id));
    }

    @PutMapping("/{id}/enviar")
    @Operation(summary = "Marcar pedido como enviado")
    public ResponseEntity<PedidoResponse> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.enviar(id));
    }

    @PutMapping("/{id}/entregar")
    @Operation(summary = "Marcar pedido como entregue")
    public ResponseEntity<PedidoResponse> entregar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.entregar(id));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }
}
