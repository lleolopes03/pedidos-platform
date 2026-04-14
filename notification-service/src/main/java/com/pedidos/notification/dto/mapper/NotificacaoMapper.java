package com.pedidos.notification.dto.mapper;

import com.pedidos.notification.domain.entity.Notificacao;
import com.pedidos.notification.dto.NotificacaoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {
    @Mapping(target = "status", expression = "java(notificacao.getStatus().name())")
    NotificacaoResponse toResponse(Notificacao notificacao);
}
