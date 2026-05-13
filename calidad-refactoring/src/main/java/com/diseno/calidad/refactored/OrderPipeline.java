package com.diseno.calidad.refactored;

import com.diseno.calidad.pipeline.DiscountStage;
import com.diseno.calidad.pipeline.PaymentStage;
import com.diseno.calidad.pipeline.PersistenceStage;
import com.diseno.calidad.pipeline.ShippingStage;
import com.diseno.calidad.pipeline.Stage;
import com.diseno.calidad.pipeline.SubtotalStage;
import com.diseno.calidad.pipeline.ValidationStage;

/**
 * Pipeline de procesamiento de pedidos.
 * Ensambla las etapas en orden secuencial usando composición funcional.
 *
 * <p>Flujo: Validación → Subtotal → Descuento → Envío → Pago → Persistencia</p>
 *
 * <p>Cada etapa tiene responsabilidad única (SRP) y V(G) &lt;= 3.</p>
 */
public class OrderPipeline {

    private final Stage<OrderContext> pipeline;

    /**
     * Construye el pipeline inyectando el repositorio de clientes.
     *
     * @param repo repositorio de clientes para la etapa de descuento
     */
    public OrderPipeline(CustomerRepository repo) {
        Stage<OrderContext> validate   = new ValidationStage();
        Stage<OrderContext> subtotal   = new SubtotalStage();
        Stage<OrderContext> discount   = new DiscountStage(repo);
        Stage<OrderContext> shipping   = new ShippingStage();
        Stage<OrderContext> payment    = new PaymentStage();
        Stage<OrderContext> persist    = new PersistenceStage();

        this.pipeline = validate
                .then(subtotal)
                .then(discount)
                .then(shipping)
                .then(payment)
                .then(persist);
    }

    /**
     * Ejecuta el pipeline completo sobre un contexto de pedido.
     *
     * @param ctx contexto de entrada con datos del pedido
     * @return contexto enriquecido con todos los campos calculados
     */
    public OrderContext execute(OrderContext ctx) {
        return pipeline.process(ctx);
    }
}
