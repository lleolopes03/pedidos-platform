# Pedidos Platform

Plataforma de gestão de pedidos com arquitetura de microsserviços, mensageria assíncrona e CI/CD.

## Arquitetura

```
[Cliente] → [order-service :8080] → Kafka → [notification-service :8081]
                    ↓                                    ↓
              PostgreSQL (order_db)           PostgreSQL (notification_db)
```

## Microsserviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| order-service | 8080 | Gerenciamento de produtos e pedidos com JWT |
| notification-service | 8081 | Consumo de eventos e registro de notificações |

## Tecnologias

- Java 21 + Spring Boot 3.2
- Spring Security + JWT
- Apache Kafka (mensageria)
- PostgreSQL (banco separado por serviço)
- Resilience4j (Circuit Breaker)
- MapStruct (mapeamento DTO/Entidade)
- Swagger/OpenAPI (documentação)
- JUnit 5 + Mockito + Testcontainers (testes)
- Docker + Docker Compose (infraestrutura)
- GitHub Actions (CI/CD)

## Como rodar

### Com Docker Compose (recomendado)

```bash
docker-compose up --build
```

### Localmente

```bash
# Suba Kafka e PostgreSQL
docker-compose up postgres-order postgres-notification zookeeper kafka -d

# order-service
cd order-service && mvn spring-boot:run

# notification-service (outro terminal)
cd notification-service && mvn spring-boot:run
```

## Swagger

- Order Service: http://localhost:8080/swagger-ui.html
- Notification Service: http://localhost:8081/swagger-ui.html

## Credenciais padrão

```
username: admin
password: admin123
```

## Fluxo completo

1. POST /auth/login → obtém token JWT
2. POST /api/v1/produtos → cadastra produto
3. POST /api/v1/pedidos → cria pedido (status: PENDENTE)
4. PUT /api/v1/pedidos/{id}/confirmar → desconta estoque (status: CONFIRMADO)
5. PUT /api/v1/pedidos/{id}/enviar → (status: ENVIADO)
6. PUT /api/v1/pedidos/{id}/entregar → (status: ENTREGUE)
7. GET /api/v1/notificacoes → visualiza notificações geradas

## Testes

```bash
# order-service
cd order-service && mvn test

# notification-service
cd notification-service && mvn test
```

## Design Patterns aplicados

| Pattern | Localização | Propósito |
|---|---|---|
| Strategy | order-service/domain/strategy | Validação de transições de status |
| Factory | notification-service/factory | Geração de mensagem por tipo de evento |
| Builder | Entidades com @Builder | Construção fluente de objetos |
| Observer | Kafka Producer/Consumer | Comunicação assíncrona entre serviços |

## Decisões técnicas

- **Kafka**: desacoplamento entre serviços — order-service publica eventos sem depender do notification-service estar disponível
- **Banco separado por serviço**: independência de dados, sem acoplamento oculto via banco compartilhado
- **Circuit Breaker**: protege chamadas a sistemas externos (EstoqueExternoClient) com fallback automático para estoque local
- **JWT stateless**: autenticação sem sessão, escalável horizontalmente
- **Testcontainers**: testes de integração com PostgreSQL real, sem mock de banco

## CI/CD

O pipeline GitHub Actions executa automaticamente em todo push para `main`:
1. Build e testes dos dois serviços
2. Build das imagens Docker
3. Push para Docker Hub com tag do commit SHA

Configure os secrets no GitHub:
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`
