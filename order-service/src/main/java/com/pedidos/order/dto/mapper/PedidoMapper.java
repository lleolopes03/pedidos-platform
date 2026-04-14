package com.pedidos.order.dto.mapper;

import com.pedidos.order.domain.entity.ItemPedido;
import com.pedidos.order.domain.entity.Pedido;
import com.pedidos.order.dto.response.ItemPedidoResponse;
import com.pedidos.order.dto.response.PedidoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
    @Mapping(target = "status", expression = "java(pedido.getStatus().name())")
    PedidoResponse toResponse(Pedido pedido);

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    ItemPedidoResponse toItemResponse(ItemPedido item);
}
