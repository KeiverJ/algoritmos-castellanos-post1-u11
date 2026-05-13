package com.diseno.calidad.original;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para la clase original {@link OrderProcessor}.
 * Verifican la funcionalidad antes de la refactorización.
 */
class OrderProcessorTest {

    private OrderProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OrderProcessor();
    }

    // --- Ítems de prueba reutilizables ---

    private List<Map<String, Object>> singleItem(double price, int qty) {
        return List.of(Map.of("price", price, "quantity", qty));
    }

    private List<Map<String, Object>> multipleItems() {
        return List.of(
                Map.of("price", 25.0, "quantity", 2),
                Map.of("price", 50.0, "quantity", 1)
        );
    }

    // --- Validación ---

    @Test
    @DisplayName("Lanza excepción si customerId es null")
    void testNullCustomerId() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.processOrder(null, singleItem(10.0, 1),
                        null, "CASH", "Calle 1", false));
    }

    @Test
    @DisplayName("Lanza excepción si customerId está vacío")
    void testBlankCustomerId() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.processOrder("  ", singleItem(10.0, 1),
                        null, "CASH", "Calle 1", false));
    }

    @Test
    @DisplayName("Lanza excepción si items es null")
    void testNullItems() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.processOrder("C001", null,
                        null, "CASH", "Calle 1", false));
    }

    @Test
    @DisplayName("Lanza excepción si items está vacío")
    void testEmptyItems() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.processOrder("C001", Collections.emptyList(),
                        null, "CASH", "Calle 1", false));
    }

    // --- Pedido sin descuento ---

    @Test
    @DisplayName("Pedido simple sin descuento ni express (subtotal < 100)")
    void testSimpleOrderNoDiscount() {
        // subtotal = 10*1 = 10, shipping = 5.99, total = 10 + 5.99 = 15.99
        double total = processor.processOrder("C001", singleItem(10.0, 1),
                null, "CASH", "Calle 1", false);
        assertEquals(15.99, total, 0.01);
    }

    @Test
    @DisplayName("Pedido sin descuento con envío gratuito (subtotal >= 100)")
    void testFreeShippingOver100() {
        // subtotal = 50*3 = 150, shipping = 0, total = 150
        double total = processor.processOrder("C001", singleItem(50.0, 3),
                null, "CASH", "Calle 1", false);
        assertEquals(150.0, total, 0.01);
    }

    // --- Descuentos ---

    @Test
    @DisplayName("PROMO10 aplica 10% de descuento")
    void testPromo10() {
        // subtotal = 100, discount = 10%, shipping = 0
        // total = 100 * 0.90 + 0 = 90
        double total = processor.processOrder("C001", singleItem(50.0, 2),
                "PROMO10", "CASH", "Calle 1", false);
        assertEquals(90.0, total, 0.01);
    }

    @Test
    @DisplayName("PROMO20 aplica 20% de descuento")
    void testPromo20() {
        // subtotal = 100, discount = 20%, shipping = 0
        // total = 100 * 0.80 = 80
        double total = processor.processOrder("C001", singleItem(50.0, 2),
                "PROMO20", "CASH", "Calle 1", false);
        assertEquals(80.0, total, 0.01);
    }

    @Test
    @DisplayName("VIP con cliente no VIP aplica 5% de descuento")
    void testVipNonVipCustomer() {
        // isVipCustomer retorna false → discount = 5%
        // subtotal = 100, total = 100 * 0.95 = 95
        double total = processor.processOrder("C001", singleItem(50.0, 2),
                "VIP", "CASH", "Calle 1", false);
        assertEquals(95.0, total, 0.01);
    }

    @Test
    @DisplayName("Código de descuento inválido no aplica descuento")
    void testInvalidDiscountCode() {
        // subtotal = 100, discount = 0%, shipping = 0
        double total = processor.processOrder("C001", singleItem(50.0, 2),
                "INVALIDO", "CASH", "Calle 1", false);
        assertEquals(100.0, total, 0.01);
    }

    // --- Envío express ---

    @Test
    @DisplayName("Envío express cobra $15 independientemente del subtotal")
    void testExpressShipping() {
        // subtotal = 200, discount = 0%, shipping = 15
        // total = 200 + 15 = 215
        double total = processor.processOrder("C001", singleItem(100.0, 2),
                null, "CASH", "Calle 1", true);
        assertEquals(215.0, total, 0.01);
    }

    // --- Tipos de pago ---

    @Test
    @DisplayName("CREDIT agrega 3% de recargo")
    void testCreditPayment() {
        // subtotal = 100, shipping = 0, base = 100
        // total = 100 * 1.03 = 103
        double total = processor.processOrder("C001", singleItem(50.0, 2),
                null, "CREDIT", "Calle 1", false);
        assertEquals(103.0, total, 0.01);
    }

    @Test
    @DisplayName("CRYPTO aplica 2% de descuento")
    void testCryptoPayment() {
        // subtotal = 100, shipping = 0, base = 100
        // total = 100 * 0.98 = 98
        double total = processor.processOrder("C001", singleItem(50.0, 2),
                null, "CRYPTO", "Calle 1", false);
        assertEquals(98.0, total, 0.01);
    }

    // --- Caso combinado ---

    @Test
    @DisplayName("Pedido con PROMO10, CREDIT y express")
    void testCombinedScenario() {
        // subtotal = 25*2 + 50*1 = 100
        // discount = 10% → 100 * 0.90 = 90
        // shipping = 15 (express)
        // base = 90 + 15 = 105
        // CREDIT → 105 * 1.03 = 108.15
        double total = processor.processOrder("C001", multipleItems(),
                "PROMO10", "CREDIT", "Calle 1", true);
        assertEquals(108.15, total, 0.01);
    }
}
