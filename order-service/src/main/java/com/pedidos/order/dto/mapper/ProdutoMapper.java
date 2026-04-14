package com.pedidos.order.dto.mapper;

import com.pedidos.order.domain.entity.Produto;
import com.pedidos.order.dto.request.ProdutoRequest;
import com.pedidos.order.dto.response.ProdutoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {
    Produto toEntity(ProdutoRequest request);
    ProdutoResponse toResponse(Produto produto);
}
