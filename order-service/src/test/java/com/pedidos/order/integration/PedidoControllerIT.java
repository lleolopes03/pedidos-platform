package com.pedidos.order.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedidos.order.dto.request.ItemPedidoRequest;
import com.pedidos.order.dto.request.LoginRequest;
import com.pedidos.order.dto.request.PedidoRequest;
import com.pedidos.order.dto.request.ProdutoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"pedido-status"})
@ActiveProfiles("test")
class PedidoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername("admin");
        login.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andReturn();

        String body = result.getResponse().getContentAsString();
        token = "Bearer " + objectMapper.readTree(body).get("token").asText();
    }

    private Long criarProduto() throws Exception {
        ProdutoRequest req = new ProdutoRequest();
        req.setNome("Produto Teste");
        req.setPreco(new BigDecimal("50.00"));
        req.setEstoque(100);

        MvcResult result = mockMvc.perform(post("/api/v1/produtos")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void criarPedido_deveRetornar201() throws Exception {
        Long produtoId = criarProduto();

        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(produtoId);
        item.setQuantidade(2);

        PedidoRequest req = new PedidoRequest();
        req.setClienteNome("Joao Silva");
        req.setClienteEmail("joao@email.com");
        req.setItens(List.of(item));

        mockMvc.perform(post("/api/v1/pedidos")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void confirmarPedido_deveRetornar200() throws Exception {
        Long produtoId = criarProduto();

        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(produtoId);
        item.setQuantidade(1);

        PedidoRequest req = new PedidoRequest();
        req.setClienteNome("Maria");
        req.setClienteEmail("maria@email.com");
        req.setItens(List.of(item));

        MvcResult criado = mockMvc.perform(post("/api/v1/pedidos")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andReturn();

        Long pedidoId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/v1/pedidos/" + pedidoId + "/confirmar")
            .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    void cancelarPedidoEntregue_deveRetornar400() throws Exception {
        Long produtoId = criarProduto();

        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(produtoId);
        item.setQuantidade(1);

        PedidoRequest req = new PedidoRequest();
        req.setClienteNome("Carlos");
        req.setClienteEmail("carlos@email.com");
        req.setItens(List.of(item));

        MvcResult criado = mockMvc.perform(post("/api/v1/pedidos")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andReturn();

        Long pedidoId = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/v1/pedidos/" + pedidoId + "/confirmar").header("Authorization", token));
        mockMvc.perform(put("/api/v1/pedidos/" + pedidoId + "/enviar").header("Authorization", token));
        mockMvc.perform(put("/api/v1/pedidos/" + pedidoId + "/entregar").header("Authorization", token));

        mockMvc.perform(put("/api/v1/pedidos/" + pedidoId + "/cancelar")
            .header("Authorization", token))
            .andExpect(status().isBadRequest());
    }
}
