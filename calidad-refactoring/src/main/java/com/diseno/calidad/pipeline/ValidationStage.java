package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.OrderContext;

import java.util.Objects;

/**
 * Etapa de validación del pipeline.
 * Verifica que el contexto de entrada tenga los datos mínimos requeridos.
 * V(G) esperado: 3 (tres caminos: null, blank, items vacíos).
 */
public class ValidationStage implements Stage<OrderContext> {

    /**
     * Valida que el customerId no sea nulo ni vacío y que los ítems no estén vacíos.
     *
     * @param ctx contexto de pedido a validar
     * @return el mismo contexto si la validación es exitosa
     * @throws NullPointerException     si customerId es null
     * @throws IllegalArgumentException si customerId está vacío o items están vacíos
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        Objects.requireNonNull(ctx.customerId(), "customerId requerido");

        if (ctx.customerId().isBlank()) {
            throw new IllegalArgumentException("customerId no puede estar vacío");
        }
        if (ctx.items() == null || ctx.items().isEmpty()) {
            throw new IllegalArgumentException("items no pueden estar vacíos");
        }

        return ctx;
    }
}
