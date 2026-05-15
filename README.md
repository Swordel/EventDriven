# Atividade prática: arquitetura event-driven com Java, RabbitMQ e GitHub Codespaces

Este repositório contém uma atividade prática para alunos de graduação explorarem os fundamentos de arquitetura orientada a eventos em um cenário simples de pedidos. A aplicação usa Java 21, Spring Boot, Spring AMQP, RabbitMQ e GitHub Codespaces para que todo o ambiente já esteja pronto ao abrir o projeto.

## Índice

- [Objetivos de aprendizagem](#objetivos-de-aprendizagem)
- [Fluxo da aplicação](#fluxo-da-aplicação)
- [Critérios de avaliação](#critérios-de-avaliação)
- [Observações didáticas](#observações-didáticas)
- [00 — Visão geral](#00--visão-geral)
- [01 — Conceitos](#01--conceitos)
- [02 — Ambiente](#02--ambiente)
- [03 — Execução](#03--execução)
- [04 — Tarefas](#04--tarefas)
- [05 — Desafios](#05--desafios)
- [06 — Discussão](#06--discussão)

## Objetivos de aprendizagem

Ao final da atividade, espera-se que o aluno consiga:

- entender o papel de produtores de eventos;
- entender o papel de consumidores de eventos;
- identificar exchanges e filas no RabbitMQ;
- explicar o desacoplamento entre componentes;
- observar múltiplos consumidores reagindo ao mesmo evento.

## Fluxo da aplicação

```text
POST /orders
   |
   v
OrderController
   |
   v
orders.exchange
   |
   +--> stock.queue        -> StockConsumer
   +--> payment.queue      -> PaymentConsumer
   +--> notification.queue -> NotificationConsumer
```

O produtor publica um único `OrderCreatedEvent`. A exchange do tipo `fanout` replica esse evento para três filas independentes.

## Critérios de avaliação

- A aplicação executa no Codespaces.
- O aluno consegue publicar um evento.
- O aluno identifica exchange, filas e consumidores.
- O aluno implementa ou altera um consumidor.
- O aluno explica o desacoplamento obtido.

## Observações didáticas

O projeto inicial funciona sem modificações. Alguns `TODOs` foram deixados no código para ampliar o fluxo durante a prática, como publicar novos eventos, criar novos consumidores e substituir a exchange `fanout` por uma `topic` em desafios posteriores.

---

## 00 — Visão geral

### Proposta

Nesta atividade, você irá explorar os fundamentos de uma arquitetura orientada a eventos por meio de uma aplicação simples de criação de pedidos. Em vez de um componente chamar diretamente todos os outros, o sistema publica um evento e deixa que diferentes consumidores reajam a ele de forma independente.

### Duração estimada

De **90 a 120 minutos**.

### Pré-requisitos

- noções básicas de Java;
- noções básicas de HTTP;
- noções básicas de Spring Boot são desejáveis, mas não obrigatórias.

### Domínio usado

O domínio é o de **criação de pedidos**. Quando um pedido é recebido:

1. a aplicação cria um evento `OrderCreatedEvent`;
2. publica esse evento no RabbitMQ;
3. três consumidores reagem de forma independente:
   - estoque;
   - pagamento;
   - notificação.

Esse domínio é pequeno o bastante para caber em uma primeira prática, mas próximo o bastante de sistemas reais para tornar os conceitos concretos.

---

## 01 — Conceitos

### Evento

Um evento representa algo que **já aconteceu** no sistema. No domínio desta atividade, `OrderCreatedEvent` significa que um pedido foi criado.

### Produtor

O produtor é quem publica o evento. Aqui, o `OrderController` recebe a requisição HTTP e publica o evento no RabbitMQ.

### Consumidor

O consumidor reage a um evento recebido. Nesta atividade:

- `StockConsumer` simula reserva de estoque;
- `PaymentConsumer` simula processamento de pagamento;
- `NotificationConsumer` simula envio de notificação.

### Broker

O broker é o intermediário responsável por receber, rotear e entregar mensagens. O broker usado aqui é o **RabbitMQ**.

### Exchange

A exchange recebe mensagens publicadas pelos produtores e decide para onde enviá-las. A aplicação usa a exchange `orders.exchange`.

### Fila

A fila armazena mensagens até que um consumidor as processe. Nesta atividade existem:

- `stock.queue`;
- `payment.queue`;
- `notification.queue`.

### Binding

Um binding conecta uma exchange a uma fila. É ele que define quais filas recebem as mensagens enviadas para uma exchange.

### Fanout exchange

Uma exchange do tipo `fanout` envia cada mensagem para **todas** as filas ligadas a ela. Assim, um único pedido criado chega ao estoque, ao pagamento e à notificação.

### Desacoplamento

O `OrderController` não precisa conhecer os consumidores. Ele apenas publica um evento. Isso permite:

- adicionar novos consumidores sem alterar o produtor;
- evoluir cada parte do sistema separadamente;
- reduzir dependências diretas entre componentes.

Em sistemas maiores, esse desacoplamento melhora a flexibilidade, mas também traz novos desafios de observabilidade, consistência e tratamento de falhas.

---

## 02 — Ambiente

### Abrindo o Codespace

1. Abra o repositório no GitHub.
2. Clique em **Code**.
3. Abra a aba **Codespaces**.
4. Crie um novo Codespace.
5. Aguarde o ambiente terminar de iniciar.

### Containers iniciados

O ambiente usa dois containers:

#### `app`

Container de desenvolvimento com Java 21 e Maven. É nele que você edita o código e executa a aplicação Spring Boot.

#### `rabbitmq`

Container com RabbitMQ e a interface web de administração.

### Portas usadas

- `8080`: aplicação Spring Boot;
- `5672`: comunicação AMQP entre a aplicação e o RabbitMQ;
- `15672`: interface web RabbitMQ Management.

O Codespaces encaminha as portas necessárias para que a aplicação e a interface de administração possam ser acessadas no navegador.

---

## 03 — Execução

### Executar a aplicação

No terminal:

```bash
mvn spring-boot:run
```

### Testar com `requests.http`

Abra o arquivo `requests.http` e execute a requisição `POST /orders` usando a extensão REST Client do VS Code.

### Testar com curl

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "productId": "P001",
    "quantity": 2
  }'
```

Resposta esperada:

```json
{
  "orderId": "uuid-gerado",
  "status": "EVENT_PUBLISHED",
  "message": "OrderCreatedEvent publicado com sucesso"
}
```

### Acessar RabbitMQ Management

Abra a porta encaminhada `15672` no Codespaces e faça login com:

- usuário: `guest`
- senha: `guest`

### O que observar

Na interface:

- a exchange `orders.exchange`;
- as filas `stock.queue`, `payment.queue` e `notification.queue`;
- os bindings entre a exchange e as filas;
- o número de mensagens publicadas e consumidas.

Nos logs da aplicação, observe como os três consumidores reagem ao mesmo evento.

---

## 04 — Tarefas

### Tarefa 1 — Executar o projeto

**Duração:** 10 minutos  
Abra o Codespace e execute a aplicação.

### Tarefa 2 — Enviar primeiro pedido

**Duração:** 10 minutos  
Envie `POST /orders` e observe os logs da aplicação.

### Tarefa 3 — Localizar produtor de eventos

**Duração:** 10 minutos  
Identifique onde o `OrderCreatedEvent` é criado e publicado.

### Tarefa 4 — Localizar consumidores

**Duração:** 15 minutos  
Encontre `StockConsumer`, `PaymentConsumer` e `NotificationConsumer`.

### Tarefa 5 — Modificar um consumidor

**Duração:** 15 a 20 minutos  
Altere o `StockConsumer` para rejeitar a reserva quando `quantity > 5`, apenas imprimindo um log. Não é necessário persistir estado.

### Tarefa 6 — Criar ou completar um novo evento

**Duração:** 20 a 25 minutos  
Use `PaymentApprovedEvent` como base. Modifique o `PaymentConsumer` para simular a aprovação do pagamento.

Se a implementação completa exceder o tempo da turma, trate esta tarefa como desafio guiado: discuta onde o novo evento seria criado, em qual exchange seria publicado e quem poderia consumi-lo.

### Tarefa 7 — Discussão

**Duração:** 10 a 15 minutos

- O `OrderController` conhece os consumidores?
- O que acontece se adicionarmos um novo consumidor?
- O que acontece se um consumidor falhar?
- Quais vantagens e riscos existem nesse modelo?

---

## 05 — Desafios

### Desafio 1 — Criar uma nova fila de auditoria

- Criar `audit.queue`;
- criar `AuditConsumer`;
- ligar a fila à exchange;
- registrar todo `OrderCreatedEvent`.

### Desafio 2 — Criar evento de pagamento aprovado

- alterar `PaymentConsumer`;
- publicar `PaymentApprovedEvent`;
- criar nova exchange ou reutilizar uma exchange;
- criar consumidor para notificação de pagamento aprovado.

### Desafio 3 — Trocar fanout exchange por topic exchange

- criar routing keys:
  - `order.created`;
  - `payment.approved`;
  - `payment.rejected`;
- explicar a diferença entre `fanout` e `topic`.

### Desafio 4 — Simular falhas

- lançar exceção em um consumidor;
- observar o comportamento;
- discutir conceitualmente:
  - retry;
  - dead-letter queue;
  - idempotência.

---

## 06 — Discussão

- Por que esse modelo reduz acoplamento?
- O que fica mais difícil em arquitetura event-driven?
- Como rastrear uma transação que passa por vários consumidores?
- Como evitar processar o mesmo evento duas vezes?
- Quando uma chamada REST síncrona seria melhor?
- Quando mensageria seria melhor?
- Qual a diferença entre fila e stream?
- Por que Kafka poderia ser escolhido em outro contexto?
- Por que RabbitMQ foi escolhido para esta atividade?
