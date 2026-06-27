package com.universidad.inventario.exception;

/**
 * Excepción personalizada lanzada cuando no hay stock suficiente
 * para registrar una transacción de tipo SALIDA.
 *
 * Es una RuntimeException para que Spring pueda gestionarla sin
 * obligar a declarar throws en cada método de la capa Service.
 */
public class StockInsuficienteException extends RuntimeException {

    private final String codigoSku;
    private final int stockDisponible;
    private final int cantidadSolicitada;

    public StockInsuficienteException(String codigoSku, int stockDisponible, int cantidadSolicitada) {
        super(String.format(
            "Stock insuficiente para el producto con SKU '%s'. " +
            "Stock disponible: %d, Cantidad solicitada: %d.",
            codigoSku, stockDisponible, cantidadSolicitada
        ));
        this.codigoSku = codigoSku;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getCodigoSku() { return codigoSku; }
    public int getStockDisponible() { return stockDisponible; }
    public int getCantidadSolicitada() { return cantidadSolicitada; }
}
