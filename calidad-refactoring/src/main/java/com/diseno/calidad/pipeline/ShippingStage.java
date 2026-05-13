package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.OrderContext;

/**
 * Etapa de cálculo de costo de envío del pipeline.
 * Determina el costo según si es express o el subtotal.
 * V(G) esperado: 3 (tres caminos: express, gratuito, estándar).
 */
public class ShippingStage implements Stage<OrderContext> {

    private static final double EXPRESS_COST = 15.0;
    private static final double STANDARD_COST = 5.99;
    private static final double FREE_SHIPPING_THRESHOLD = 100.0;

    /**
     * Calcula el costo de envío según las reglas de negocio:
     * <ul>
     *   <li>Express: $15.00</li>
     *   <li>Subtotal &gt;= $100: envío gratuito</li>
     *   <li>Otro caso: $5.99</li>
     * </ul>
     *
     * @param ctx contexto con subtotal calculado y flag express
     * @return contexto enriquecido con el costo de envío
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        double shippingCost;

        if (ctx.express()) {
            shippingCost = EXPRESS_COST;
        } else if (ctx.subtotal() >= FREE_SHIPPING_THRESHOLD) {
            shippingCost = 0.0;
        } else {
            shippingCost = STANDARD_COST;
        }

        return ctx.withShippingCost(shippingCost);
    }
}
