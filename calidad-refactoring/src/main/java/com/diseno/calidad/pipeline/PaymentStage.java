package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.OrderContext;

/**
 * Etapa de procesamiento de pago del pipeline.
 * Aplica modificadores según el tipo de pago sobre el total parcial.
 * V(G) esperado: 3 (tres caminos: CREDIT, CRYPTO, otro).
 */
public class PaymentStage implements Stage<OrderContext> {

    private static final double CREDIT_SURCHARGE = 1.03;
    private static final double CRYPTO_DISCOUNT = 0.98;

    /**
     * Calcula el total final aplicando modificadores de pago:
     * <ul>
     *   <li>CREDIT: +3% de recargo</li>
     *   <li>CRYPTO: -2% de descuento</li>
     *   <li>Otro: sin modificador</li>
     * </ul>
     *
     * @param ctx contexto con subtotal, discountFactor y shippingCost
     * @return contexto con total final calculado
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        double total = ctx.subtotal() * (1 - ctx.discountFactor()) + ctx.shippingCost();

        if ("CREDIT".equals(ctx.paymentType())) {
            total *= CREDIT_SURCHARGE;
        } else if ("CRYPTO".equals(ctx.paymentType())) {
            total *= CRYPTO_DISCOUNT;
        }

        return ctx.withTotal(total);
    }
}
