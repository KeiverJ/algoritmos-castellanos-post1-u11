package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.OrderContext;

/**
 * Etapa de validación del pipeline.
 * Verifica que el contexto de entrada tenga los datos mínimos requeridos.
 * Cada método auxiliar tiene V(G) &lt;= 3 según PMD.
 */
public class ValidationStage implements Stage<OrderContext> {

    /**
     * Valida que el contexto tenga un cliente y ítems válidos.
     *
     * @param ctx contexto de pedido a validar
     * @return el mismo contexto si la validación es exitosa
     * @throws IllegalArgumentException si los datos son inválidos
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        java.util.Objects.requireNonNull(ctx.customerId(), "customerId requerido");
        java.util.Objects.requireNonNull(ctx.items(), "items no pueden ser null");

        if (ctx.customerId().isBlank()) {
            throw new IllegalArgumentException("customerId vacío");
        }
        if (ctx.items().isEmpty()) {
            throw new IllegalArgumentException("items vacíos");
        }

        return ctx;
    }
}
