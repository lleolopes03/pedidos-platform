package com.pedidos.order.service;

import com.pedidos.order.domain.entity.Produto;
import com.pedidos.order.dto.mapper.ProdutoMapper;
import com.pedidos.order.dto.request.ProdutoRequest;
import com.pedidos.order.dto.response.ProdutoResponse;
import com.pedidos.order.exception.ResourceNotFoundException;
import com.pedidos.order.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    @Transactional
    public ProdutoResponse cadastrar(ProdutoRequest request) {
        Produto produto = produtoMapper.toEntity(request);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtoRepository.findAll().stream()
            .map(produtoMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return produtoMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = findOrThrow(id);
        produto.setNome(request.getNome());
        produto.setPreco(request.getPreco());
        produto.setEstoque(request.getEstoque());
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        findOrThrow(id);
        produtoRepository.deleteById(id);
    }

    public Produto findOrThrow(Long id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
    }
}
