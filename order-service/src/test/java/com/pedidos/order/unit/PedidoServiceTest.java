package com.pedidos.order.unit;

import com.pedidos.order.domain.entity.ItemPedido;
import com.pedidos.order.domain.entity.Pedido;
import com.pedidos.order.domain.entity.Produto;
import com.pedidos.order.domain.enums.StatusPedido;
import com.pedidos.order.domain.strategy.StatusTransitionValidator;
import com.pedidos.order.dto.mapper.PedidoMapper;
import com.pedidos.order.dto.request.ItemPedidoRequest;
import com.pedidos.order.dto.request.PedidoRequest;
import com.pedidos.order.exception.BusinessException;
import com.pedidos.order.kafka.PedidoEventProducer;
import com.pedidos.order.repository.PedidoRepository;
import com.pedidos.order.service.PedidoService;
import com.pedidos.order.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ProdutoService produtoService;
    @Mock private PedidoMapper pedidoMapper;
    @Mock private PedidoEventProducer eventProducer;
    @Mock private StatusTransitionValidator statusValidator;
    @InjectMocks private PedidoService pedidoService;

    @Test
    void criarPedido_devePublicarEvento_quandoSucesso() {
        Produto produto = Produto.builder().id(1L).nome("Produto A").preco(new BigDecimal("50.00")).estoque(10).build();
        when(produtoService.findOrThrow(1L)).thenReturn(produto);

        Pedido pedidoSalvo = Pedido.builder().id(1L).clienteNome("Joao").clienteEmail("joao@email.com")
            .status(StatusPedido.PENDENTE).total(new BigDecimal("100.00")).itens(new ArrayList<>()).build();
        when(pedidoRepository.save(any())).thenReturn(pedidoSalvo);
        doNothing().when(eventProducer).publicar(any());
        when(pedidoMapper.toResponse(any())).thenReturn(null);

        ItemPedidoRequest itemReq = new ItemPedidoRequest();
        itemReq.setProdutoId(1L);
        itemReq.setQuantidade(2);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("Joao");
        request.setClienteEmail("joao@email.com");
        request.setItens(List.of(itemReq));

        pedidoService.criar(request);

        verify(pedidoRepository).save(any());
        verify(eventProducer).publicar(any());
    }

    @Test
    void criarPedido_deveLancarException_quandoEstoqueInsuficiente() {
        Produto produto = Produto.builder().id(1L).nome("Produto A").preco(new BigDecimal("50.00")).estoque(1).build();
        when(produtoService.findOrThrow(1L)).thenReturn(produto);

        ItemPedidoRequest itemReq = new ItemPedidoRequest();
        itemReq.setProdutoId(1L);
        itemReq.setQuantidade(5);

        PedidoRequest request = new PedidoRequest();
        request.setClienteNome("Joao");
        request.setClienteEmail("joao@email.com");
        request.setItens(List.of(itemReq));

        assertThrows(BusinessException.class, () -> pedidoService.criar(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void confirmarPedido_deveLancarException_quandoStatusInvalido() {
        Pedido pedido = Pedido.builder().id(1L).status(StatusPedido.CANCELADO).itens(new ArrayList<>()).build();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doThrow(new BusinessException("Status invalido")).when(statusValidator).validar(StatusPedido.CONFIRMADO, StatusPedido.CANCELADO);

        assertThrows(BusinessException.class, () -> pedidoService.confirmar(1L));
    }

    @Test
    void cancelarPedido_deveRestaurarEstoque_quandoConfirmado() {
        Produto produto = Produto.builder().id(1L).estoque(5).build();
        ItemPedido item = ItemPedido.builder().produto(produto).quantidade(3).build();
        Pedido pedido = Pedido.builder().id(1L).status(StatusPedido.CONFIRMADO).itens(List.of(item))
            .clienteNome("Joao").clienteEmail("joao@email.com").total(BigDecimal.TEN).build();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        doNothing().when(statusValidator).validar(any(), any());
        when(pedidoRepository.save(any())).thenReturn(pedido);
        doNothing().when(eventProducer).publicar(any());
        when(pedidoMapper.toResponse(any())).thenReturn(null);

        pedidoService.cancelar(1L);

        assertEquals(8, produto.getEstoque());
    }
}
