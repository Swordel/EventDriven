package br.edu.eventdriven.auditoria;

import br.edu.eventdriven.configuracao.ConfiguracaoRabbitMQ;
import br.edu.eventdriven.pedido.EventoPedidoCriado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorAuditoria {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorAuditoria.class);

    @RabbitListener(queues = ConfiguracaoRabbitMQ.FILA_AUDITORIA)
    public void processar(EventoPedidoCriado evento) {
        log.info("[AUDITORIA] pedido={} | cliente={} | produto={} | quantidade={} | criadoEm={}",
                evento.idPedido(),
                evento.idCliente(),
                evento.idProduto(),
                evento.quantidade(),
                evento.criadoEm()
        );
    }
}