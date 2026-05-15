package br.edu.eventdriven.pedido;

public record SolicitacaoPedido(
        String idCliente,
        String idProduto,
        int quantidade
) {
}
