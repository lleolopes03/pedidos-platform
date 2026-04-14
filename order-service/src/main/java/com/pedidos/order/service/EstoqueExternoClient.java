package com.pedidos.order.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EstoqueExternoClient {

    @CircuitBreaker(name = "estoqueExterno", fallbackMethod = "fallbackEstoque")
    public Integer consultarEstoqueExterno(Long produtoId) {
        log.info("Consultando estoque externo para produto {}", produtoId);
        // Simula falha do sistema legado
        throw new RuntimeException("Sistema legado indisponivel");
    }

    public Integer fallbackEstoque(Long produtoId, Exception ex) {
        log.warn("Circuit Breaker ativado para produto {}. Usando estoque local. Motivo: {}", produtoId, ex.getMessage());
        return null; // null indica que deve usar o estoque local do banco
    }
}
