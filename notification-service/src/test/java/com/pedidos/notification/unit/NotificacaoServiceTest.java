package com.pedidos.notification.unit;

import com.pedidos.notification.domain.entity.Notificacao;
import com.pedidos.notification.domain.enums.StatusNotificacao;
import com.pedidos.notification.factory.MensagemFactory;
import com.pedidos.notification.kafka.PedidoEvent;
import com.pedidos.notification.repository.NotificacaoRepository;
import com.pedidos.notification.service.NotificacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock private NotificacaoRepository notificacaoRepository;
    @Mock private MensagemFactory mensagemFactory;
    @InjectMocks private NotificacaoService notificacaoService;

    @Test
    void processarEvento_deveSalvarNotificacaoComoEnviada() {
        PedidoEvent event = new PedidoEvent(1L, "joao@email.com", "Joao", "CONFIRMADO", new BigDecimal("100.00"));
        when(mensagemFactory.criar(event)).thenReturn("Pedido confirmado!");
        Notificacao salva = Notificacao.builder().id(1L).status(StatusNotificacao.ENVIADA).build();
        when(notificacaoRepository.save(any())).thenReturn(salva);

        Notificacao result = notificacaoService.processarEvento(event);

        assertEquals(StatusNotificacao.ENVIADA, result.getStatus());
        verify(notificacaoRepository).save(any());
    }

    @Test
    void processarEvento_deveSalvarComoFalha_quandoEmailNulo() {
        PedidoEvent event = new PedidoEvent(2L, null, "Maria", "ENVIADO", BigDecimal.TEN);
        Notificacao salva = Notificacao.builder().id(2L).status(StatusNotificacao.FALHA).build();
        when(notificacaoRepository.save(any())).thenReturn(salva);

        Notificacao result = notificacaoService.processarEvento(event);

        assertEquals(StatusNotificacao.FALHA, result.getStatus());
    }
}
