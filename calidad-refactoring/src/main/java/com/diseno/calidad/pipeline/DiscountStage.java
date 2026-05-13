package com.diseno.calidad.pipeline;

import com.diseno.calidad.refactored.CustomerRepository;
import com.diseno.calidad.refactored.DiscountStrategies;
import com.diseno.calidad.refactored.DiscountStrategy;
import com.diseno.calidad.refactored.OrderContext;

/**
 * Etapa de descuento del pipeline.
 * Delega la resolución del factor de descuento al patrón Strategy,
 * eliminando los condicionales del método original.
 * V(G) esperado: 1 (sin ramificaciones propias).
 */
public class DiscountStage implements Stage<OrderContext> {

    private final CustomerRepository customerRepository;

    /**
     * Crea la etapa de descuento con inyección del repositorio de clientes.
     *
     * @param customerRepository repositorio para verificar estado VIP
     */
    public DiscountStage(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Resuelve y aplica la estrategia de descuento según el código del contexto.
     *
     * @param ctx contexto con discountCode y subtotal calculado
     * @return contexto enriquecido con el factor de descuento
     */
    @Override
    public OrderContext process(OrderContext ctx) {
        DiscountStrategy strategy = DiscountStrategies.fromCode(
                ctx.discountCode(), customerRepository
        );
        double factor = strategy.discountFactor(ctx.subtotal(), ctx.customerId());

        return ctx.withDiscountFactor(factor);
    }
}
