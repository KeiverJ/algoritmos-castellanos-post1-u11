package com.diseno.calidad.original;

import java.util.List;
import java.util.Map;

/**
 * Clase original con múltiples code smells:
 * <ul>
 *   <li><b>God Class</b>: concentra validación, descuento, envío, pago y persistencia.</li>
 *   <li><b>Long Method</b>: processOrder supera las 30 líneas recomendadas.</li>
 *   <li><b>Feature Envy</b>: lógica de descuento depende de datos externos.</li>
 *   <li><b>Primitive Obsession</b>: dinero representado como double, items como Map.</li>
 * </ul>
 *
 * <p>Esta clase se conserva intacta como línea base para comparación de métricas.</p>
 */
public class OrderProcessor {

    /**
     * Procesa un pedido completo: valida, calcula descuento, envío, pago y persiste.
     *
     * @param customerId      identificador del cliente
     * @param items           lista de ítems como Map con claves "price" y "quantity"
     * @param discountCode    código de descuento (PROMO10, PROMO20, VIP o null)
     * @param paymentType     tipo de pago (CREDIT, CRYPTO u otro)
     * @param shippingAddress dirección de envío
     * @param express         indica envío exprés
     * @return total calculado del pedido
     */
    public double processOrder(String customerId,
                               List<Map<String, Object>> items,
                               String discountCode,
                               String paymentType,
                               String shippingAddress,
                               boolean express) {

        // --- Validación (debería estar en su propio objeto) ---
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId requerido");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items vacíos");
        }

        // --- Lógica de descuento embebida (Feature Envy sobre discountCode) ---
        double discount = 0.0;
        if ("PROMO10".equals(discountCode)) {
            discount = 0.10;
        } else if ("PROMO20".equals(discountCode)) {
            discount = 0.20;
        } else if ("VIP".equals(discountCode)) {
            if (isVipCustomer(customerId)) {
                discount = 0.30;
            } else {
                discount = 0.05;
            }
        }

        // --- Cálculo de subtotal ---
        double subtotal = 0.0;
        for (Map<String, Object> item : items) {
            double price = (Double) item.get("price");
            int qty = (Integer) item.get("quantity");
            subtotal += price * qty;
        }

        // --- Lógica de envío embebida ---
        double shipping = 0.0;
        if (express) {
            shipping = 15.0;
        } else if (subtotal >= 100.0) {
            shipping = 0.0;
        } else {
            shipping = 5.99;
        }

        // --- Lógica de pago embebida (Long Method) ---
        double total = subtotal * (1 - discount) + shipping;
        if ("CREDIT".equals(paymentType)) {
            total *= 1.03; // cargo por tarjeta de crédito
        } else if ("CRYPTO".equals(paymentType)) {
            total *= 0.98; // descuento por pago con criptomoneda
        }

        // --- Persistencia embebida (SRP violado) ---
        saveOrder(customerId, total, shippingAddress);

        return total;
    }

    /**
     * Verifica si el cliente es VIP consultando la base de datos.
     */
    private boolean isVipCustomer(String id) {
        // Simulación de consulta a BD
        return false;
    }

    /**
     * Persiste el pedido en el almacenamiento.
     */
    private void saveOrder(String id, double total, String addr) {
        // Simulación de persistencia
    }
}
