package com.diseno.calidad.refactored;

/**
 * Representa un ítem de pedido con precio unitario y cantidad.
 *
 * @param price    precio unitario del producto (debe ser &gt;= 0)
 * @param quantity cantidad solicitada (debe ser &gt;= 1)
 */
public record OrderItem(double price, int quantity) {

    /**
     * Crea un ítem de pedido validando restricciones.
     */
    public OrderItem {
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
    }
}
