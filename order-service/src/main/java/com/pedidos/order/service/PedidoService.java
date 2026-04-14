package com.pedidos.order.service;

import com.pedidos.order.domain.entity.ItemPedido;
import com.pedidos.order.domain.entity.Pedido;
import com.pedidos.order.domain.entity.Produto;
import com.pedidos.order.domain.enums.StatusPedido;
import com.pedidos.order.domain.strategy.StatusTransitionValidator;
import com.pedidos.order.dto.mapper.PedidoMapper;
import com.pedidos.order.dto.request.PedidoRequest;
import com.pedidos.order.dto.response.PedidoResponse;
import com.pedidos.order.exception.BusinessException;
import com.pedidos.order.exception.ResourceNotFoundException;
import com.pedidos.order.kafka.PedidoEvent;
import com.pedidos.order.kafka.PedidoEventProducer;
import com.pedidos.order.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoService produtoService;
    private final PedidoMapper pedidoMapper;
    private final PedidoEventProducer eventProducer;
    private final StatusTransitionValidator statusValidator;

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        List<ItemPedido> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        Pedido pedido = Pedido.builder()
            .clienteNome(request.getClienteNome())
            .clienteEmail(request.getClienteEmail())
            .build();

        for (var itemReq : request.getItens()) {
            Produto produto = produtoService.findOrThrow(itemReq.getProdutoId());
            if (produto.getEstoque() < itemReq.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemReq.getQuantidade()));
            total = total.add(subtotal);
            itens.add(ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(itemReq.getQuantidade())
                .precoUnitario(produto.getPreco())
                .build());
        }

        pedido.setTotal(total);
        pedido.setItens(itens);
        Pedido salvo = pedidoRepository.save(pedido);
        publicarEvento(salvo);
        return pedidoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepository.findAll().stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return pedidoMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public PedidoResponse confirmar(Long id) {
        Pedido pedido = findOrThrow(id);
        statusValidator.validar(StatusPedido.CONFIRMADO, pedido.getStatus());
        pedido.getItens().forEach(item -> {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
        });
        pedido.setStatus(StatusPedido.CONFIRMADO);
        Pedido salvo = pedidoRepository.save(pedido);
        publicarEvento(salvo);
        return pedidoMapper.toResponse(salvo);
    }

    @Transactional
    public PedidoResponse enviar(Long id) {
        Pedido pedido = findOrThrow(id);
        statusValidator.validar(StatusPedido.ENVIADO, pedido.getStatus());
        pedido.setStatus(StatusPedido.ENVIADO);
        Pedido salvo = pedidoRepository.save(pedido);
        publicarEvento(salvo);
        return pedidoMapper.toResponse(salvo);
    }

    @Transactional
    public PedidoResponse entregar(Long id) {
        Pedido pedido = findOrThrow(id);
        statusValidator.validar(StatusPedido.ENTREGUE, pedido.getStatus());
        pedido.setStatus(StatusPedido.ENTREGUE);
        Pedido salvo = pedidoRepository.save(pedido);
        publicarEvento(salvo);
        return pedidoMapper.toResponse(salvo);
    }

    @Transactional
    public PedidoResponse cancelar(Long id) {
        Pedido pedido = findOrThrow(id);
        statusValidator.validar(StatusPedido.CANCELADO, pedido.getStatus());
        if (pedido.getStatus() == StatusPedido.CONFIRMADO) {
            pedido.getItens().forEach(item -> {
                Produto produto = item.getProduto();
                produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            });
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido salvo = pedidoRepository.save(pedido);
        publicarEvento(salvo);
        return pedidoMapper.toResponse(salvo);
    }

    private void publicarEvento(Pedido pedido) {
        eventProducer.publicar(PedidoEvent.builder()
            .pedidoId(pedido.getId())
            .clienteEmail(pedido.getClienteEmail())
            .clienteNome(pedido.getClienteNome())
            .status(pedido.getStatus().name())
            .total(pedido.getTotal())
            .build());
    }

    private Pedido findOrThrow(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
    }
}
