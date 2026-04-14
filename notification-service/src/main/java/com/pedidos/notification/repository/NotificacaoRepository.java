package com.pedidos.notification.repository;

import com.pedidos.notification.domain.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByPedidoId(Long pedidoId);
}
