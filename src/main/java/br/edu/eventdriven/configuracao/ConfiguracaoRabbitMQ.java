package br.edu.eventdriven.configuracao;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings({"unused", "null"})
public class ConfiguracaoRabbitMQ {

    public static final String EXCHANGE_PEDIDOS = "orders.exchange";
    public static final String FILA_ESTOQUE = "stock.queue";
    public static final String FILA_PAGAMENTO = "payment.queue";
    public static final String FILA_NOTIFICACAO = "notification.queue";

    @Bean
    FanoutExchange exchangePedidos() {
        return new FanoutExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    Queue filaEstoque() {
        return new Queue(FILA_ESTOQUE, true);
    }

    @Bean
    Queue filaPagamento() {
        return new Queue(FILA_PAGAMENTO, true);
    }

    @Bean
    Queue filaNotificacao() {
        return new Queue(FILA_NOTIFICACAO, true);
    }

    @Bean
    Binding vinculoEstoque(Queue filaEstoque, FanoutExchange exchangePedidos) {
        return BindingBuilder.bind(filaEstoque).to(exchangePedidos);
    }

    @Bean
    Binding vinculoPagamento(Queue filaPagamento, FanoutExchange exchangePedidos) {
        return BindingBuilder.bind(filaPagamento).to(exchangePedidos);
    }

    @Bean
    Binding vinculoNotificacao(Queue filaNotificacao, FanoutExchange exchangePedidos) {
        return BindingBuilder.bind(filaNotificacao).to(exchangePedidos);
    }

    @Bean
    MessageConverter conversorMensagemJson() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate templateRabbit(ConnectionFactory connectionFactory, MessageConverter conversorMensagemJson) {
        RabbitTemplate templateRabbit = new RabbitTemplate(connectionFactory);
        templateRabbit.setMessageConverter(conversorMensagemJson);
        return templateRabbit;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter conversorMensagemJson
    ) {
        SimpleRabbitListenerContainerFactory fabrica = new SimpleRabbitListenerContainerFactory();
        fabrica.setConnectionFactory(connectionFactory);
        fabrica.setMessageConverter(conversorMensagemJson);
        return fabrica;
    }
}
