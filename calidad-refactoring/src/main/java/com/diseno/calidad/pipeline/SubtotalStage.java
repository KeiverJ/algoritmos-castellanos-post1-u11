package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.OrderContext;

/**
 * Etapa de cálculo de subtotal del pipeline.
 * Suma precio × cantidad de cada ítem usando Streams.
 * V(G) esperado: 1 (sin ramificaciones).
 */
public class SubtotalStage implements Stage<OrderContext> {

    /**
     * Calcula el subtotal sumando (precio × cantidad) de todos los ítems.
     *
     * @param ctx contexto de pedido con ítems
     * @return contexto enriquecido con el subtotal calculado
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        double subtotal = ctx.items().stream()
                .mapToDouble(item -> item.price() * item.quantity())
                .sum();

        return ctx.withSubtotal(subtotal);
    }
}
