package com.diseno.calidad.refactored;

import java.util.List;

/**
 * Objeto de contexto que fluye por el pipeline de procesamiento de pedidos.
 * Cada etapa del pipeline enriquece los campos calculados (subtotal, discountFactor,
 * shippingCost, total) y devuelve una nueva instancia inmutable.
 *
 * @param customerId      identificador del cliente (no nulo)
 * @param items           lista de ítems del pedido
 * @param discountCode    código de descuento aplicable
 * @param paymentType     tipo de pago (CREDIT, CRYPTO, CASH, etc.)
 * @param shippingAddress dirección de envío
 * @param express         indica si el envío es exprés
 * @param subtotal        subtotal calculado por SubtotalStage
 * @param discountFactor  factor de descuento calculado por DiscountStage
 * @param shippingCost    costo de envío calculado por ShippingStage
 * @param total           total final calculado por PaymentStage
 */
public record OrderContext(
        String customerId,
        List<OrderItem> items,
        String discountCode,
        String paymentType,
        String shippingAddress,
        boolean express,
        double subtotal,
        double discountFactor,
        double shippingCost,
        double total
) {

    /**
     * Constructor de entrada: crea un contexto sin campos calculados.
     *
     * @param customerId      identificador del cliente
     * @param items           lista de ítems del pedido
     * @param discountCode    código de descuento
     * @param paymentType     tipo de pago
     * @param shippingAddress dirección de envío
     * @param express         envío exprés
     * @return contexto inicializado con campos calculados en cero
     */
    public static OrderContext of(String customerId,
                                  List<OrderItem> items,
                                  String discountCode,
                                  String paymentType,
                                  String shippingAddress,
                                  boolean express) {
        return new OrderContext(
                customerId, items, discountCode, paymentType,
                shippingAddress, express, 0.0, 0.0, 0.0, 0.0
        );
    }

    /**
     * Crea una copia de este contexto con un nuevo subtotal.
     */
    public OrderContext withSubtotal(double subtotal) {
        return new OrderContext(customerId, items, discountCode, paymentType,
                shippingAddress, express, subtotal, discountFactor, shippingCost, total);
    }

    /**
     * Crea una copia de este contexto con un nuevo factor de descuento.
     */
    public OrderContext withDiscountFactor(double discountFactor) {
        return new OrderContext(customerId, items, discountCode, paymentType,
                shippingAddress, express, subtotal, discountFactor, shippingCost, total);
    }

    /**
     * Crea una copia de este contexto con un nuevo costo de envío.
     */
    public OrderContext withShippingCost(double shippingCost) {
        return new OrderContext(customerId, items, discountCode, paymentType,
                shippingAddress, express, subtotal, discountFactor, shippingCost, total);
    }

    /**
     * Crea una copia de este contexto con un nuevo total.
     */
    public OrderContext withTotal(double total) {
        return new OrderContext(customerId, items, discountCode, paymentType,
                shippingAddress, express, subtotal, discountFactor, shippingCost, total);
    }
}
