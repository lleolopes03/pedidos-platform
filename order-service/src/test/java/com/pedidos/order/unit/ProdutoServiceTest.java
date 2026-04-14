package com.pedidos.order.unit;

import com.pedidos.order.domain.entity.Produto;
import com.pedidos.order.dto.mapper.ProdutoMapper;
import com.pedidos.order.dto.request.ProdutoRequest;
import com.pedidos.order.dto.response.ProdutoResponse;
import com.pedidos.order.repository.ProdutoRepository;
import com.pedidos.order.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock private ProdutoRepository produtoRepository;
    @Mock private ProdutoMapper produtoMapper;
    @InjectMocks private ProdutoService produtoService;

    @Test
    void cadastrar_deveSalvarERetornarResponse() {
        ProdutoRequest request = new ProdutoRequest();
        request.setNome("Produto A");
        request.setPreco(new BigDecimal("99.90"));
        request.setEstoque(10);

        Produto produto = Produto.builder().id(1L).nome("Produto A").preco(new BigDecimal("99.90")).estoque(10).build();
        ProdutoResponse response = new ProdutoResponse();
        response.setId(1L);
        response.setNome("Produto A");

        when(produtoMapper.toEntity(request)).thenReturn(produto);
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toResponse(produto)).thenReturn(response);

        ProdutoResponse result = produtoService.cadastrar(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(produtoRepository).save(produto);
    }

    @Test
    void listar_deveRetornarListaDeProdutos() {
        Produto p1 = Produto.builder().id(1L).nome("A").preco(BigDecimal.TEN).estoque(5).build();
        Produto p2 = Produto.builder().id(2L).nome("B").preco(BigDecimal.ONE).estoque(3).build();
        ProdutoResponse r1 = new ProdutoResponse(); r1.setId(1L);
        ProdutoResponse r2 = new ProdutoResponse(); r2.setId(2L);

        when(produtoRepository.findAll()).thenReturn(List.of(p1, p2));
        when(produtoMapper.toResponse(p1)).thenReturn(r1);
        when(produtoMapper.toResponse(p2)).thenReturn(r2);

        List<ProdutoResponse> result = produtoService.listar();

        assertEquals(2, result.size());
    }
}
