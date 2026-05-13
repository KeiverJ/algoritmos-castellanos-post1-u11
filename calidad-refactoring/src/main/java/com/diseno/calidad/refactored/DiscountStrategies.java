package com.diseno.calidad.refactored;

/**
 * Fábrica de estrategias de descuento concretas.
 * Centraliza la resolución de código de descuento a estrategia
 * usando un switch expression de Java 17.
 */
public final class DiscountStrategies {

    private DiscountStrategies() {
        // Clase de utilidad, no instanciable
    }

    /**
     * Estrategia con factor fijo.
     *
     * @param factor factor de descuento fijo (0.0–1.0)
     * @return estrategia que siempre retorna el factor dado
     */
    public static DiscountStrategy fixed(double factor) {
        return (subtotal, id) -> factor;
    }

    /**
     * Estrategia VIP: aplica un factor diferente según si el cliente es VIP.
     *
     * @param repo          repositorio de clientes para consultar estado VIP
     * @param vipFactor     factor de descuento para clientes VIP
     * @param defaultFactor factor de descuento para clientes no VIP
     * @return estrategia condicional basada en estado VIP
     */
    public static DiscountStrategy vip(CustomerRepository repo,
                                       double vipFactor,
                                       double defaultFactor) {
        return (subtotal, id) -> repo.isVip(id) ? vipFactor : defaultFactor;
    }

    /**
     * Estrategia sin descuento.
     *
     * @return estrategia que siempre retorna factor 0.0
     */
    public static DiscountStrategy noDiscount() {
        return (subtotal, id) -> 0.0;
    }

    /**
     * Resuelve la estrategia de descuento a partir del código proporcionado.
     * Cada caso del switch tiene complejidad V(G) = 1.
     *
     * @param code código de descuento (puede ser null)
     * @param repo repositorio de clientes para estrategia VIP
     * @return estrategia correspondiente al código
     */
    public static DiscountStrategy fromCode(String code, CustomerRepository repo) {
        return switch (code == null ? "" : code) {
            case "PROMO10" -> fixed(0.10);
            case "PROMO20" -> fixed(0.20);
            case "VIP"     -> vip(repo, 0.30, 0.05);
            default        -> noDiscount();
        };
    }
}
