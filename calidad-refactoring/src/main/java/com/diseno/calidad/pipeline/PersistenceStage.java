package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.OrderContext;

/**
 * Etapa de persistencia del pipeline.
 * Simula el almacenamiento del pedido procesado.
 * V(G) esperado: 1 (sin ramificaciones).
 *
 * <p>En una implementación real, esta etapa delegaría
 * a un repositorio de pedidos (OrderRepository).</p>
 */
public class PersistenceStage implements Stage<OrderContext> {

    /**
     * Simula la persistencia del pedido y retorna el contexto sin modificar.
     *
     * @param ctx contexto con todos los campos calculados
     * @return el mismo contexto (la persistencia no altera valores)
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        // Simulación: en producción se inyectaría un OrderRepository
        System.out.printf("Pedido persistido: cliente=%s, total=%.2f, dirección=%s%n",
                ctx.customerId(), ctx.total(), ctx.shippingAddress());
        return ctx;
    }
}
