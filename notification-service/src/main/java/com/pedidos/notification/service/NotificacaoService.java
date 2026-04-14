package com.pedidos.notification.service;

import com.pedidos.notification.domain.entity.Notificacao;
import com.pedidos.notification.domain.enums.StatusNotificacao;
import com.pedidos.notification.factory.MensagemFactory;
import com.pedidos.notification.kafka.PedidoEvent;
import com.pedidos.notification.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final MensagemFactory mensagemFactory;

    @Transactional
    public Notificacao processarEvento(PedidoEvent event) {
        StatusNotificacao status;
        String mensagem;

        try {
            if (event.getClienteEmail() == null || event.getClienteEmail().isBlank()) {
                throw new IllegalArgumentException("Email do cliente nao pode ser nulo");
            }
            mensagem = mensagemFactory.criar(event);
            log.info("Notificacao enviada para {} - Pedido #{} - Status: {}",
                event.getClienteEmail(), event.getPedidoId(), event.getStatus());
            status = StatusNotificacao.ENVIADA;
        } catch (Exception ex) {
            log.error("Falha ao processar notificacao para pedido {}: {}", event.getPedidoId(), ex.getMessage());
            mensagem = "Falha ao gerar mensagem: " + ex.getMessage();
            status = StatusNotificacao.FALHA;
        }

        return notificacaoRepository.save(Notificacao.builder()
            .pedidoId(event.getPedidoId())
            .clienteEmail(event.getClienteEmail() != null ? event.getClienteEmail() : "desconhecido")
            .mensagem(mensagem)
            .status(status)
            .dataEnvio(LocalDateTime.now())
            .build());
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarTodas() {
        return notificacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarPorPedido(Long pedidoId) {
        return notificacaoRepository.findByPedidoId(pedidoId);
    }
}
