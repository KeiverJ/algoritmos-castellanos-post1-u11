package com.diseno.calidad.refactored;

/**
 * Estrategia funcional para calcular factores de descuento.
 * Implementa el patrón Strategy para eliminar condicionales
 * de descuento del método monolítico original.
 */
@FunctionalInterface
public interface DiscountStrategy {

    /**
     * Calcula el factor de descuento a aplicar.
     *
     * @param subtotal   monto antes de descuento (&gt;= 0)
     * @param customerId identificador del cliente (no nulo)
     * @return factor de descuento entre 0.0 y 1.0
     */
    double discountFactor(double subtotal, String customerId);
}
