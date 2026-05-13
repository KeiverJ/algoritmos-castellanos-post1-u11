package com.diseno.calidad.refactored;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el pipeline completo {@link OrderPipeline}.
 * Verifica el flujo extremo a extremo con diferentes combinaciones de
 * descuentos, tipos de pago y opciones de envío.
 */
class OrderPipelineTest {

    private static final String DEFAULT_ADDRESS = "Calle 1";
    private static final CustomerRepository NEVER_VIP = id -> false;
    private static final CustomerRepository ALWAYS_VIP = id -> true;

    private OrderPipeline pipeline;

    @BeforeEach
    void setUp() {
        pipeline = new OrderPipeline(NEVER_VIP);
    }

    // --- Helpers ---

    private OrderContext createContext(String discountCode, String paymentType,
                                      boolean express) {
        return OrderContext.of(
                "C001",
                List.of(new OrderItem(50.0, 2)), // subtotal = 100
                discountCode, paymentType, DEFAULT_ADDRESS, express
        );
    }

    private OrderContext createContextWithItems(List<OrderItem> items,
                                                String discountCode,
                                                String paymentType,
                                                boolean express) {
        return OrderContext.of("C001", items, discountCode, paymentType,
                DEFAULT_ADDRESS, express);
    }

    // --- Validación ---

    @Nested
    @DisplayName("Validación de entrada")
    class ValidationTests {

        @Test
        @DisplayName("Lanza NullPointerException si customerId es null")
        void testNullCustomerId() {
            OrderContext ctx = OrderContext.of(null,
                    List.of(new OrderItem(10.0, 1)),
                    null, "CASH", DEFAULT_ADDRESS, false);
            assertThrows(NullPointerException.class, () -> pipeline.execute(ctx));
        }

        @Test
        @DisplayName("Lanza excepción si customerId está vacío")
        void testBlankCustomerId() {
            OrderContext ctx = OrderContext.of("  ",
                    List.of(new OrderItem(10.0, 1)),
                    null, "CASH", DEFAULT_ADDRESS, false);
            assertThrows(IllegalArgumentException.class, () -> pipeline.execute(ctx));
        }

        @Test
        @DisplayName("Lanza excepción si items es null")
        void testNullItems() {
            OrderContext ctx = OrderContext.of("C001", null,
                    null, "CASH", DEFAULT_ADDRESS, false);
            assertThrows(IllegalArgumentException.class, () -> pipeline.execute(ctx));
        }

        @Test
        @DisplayName("Lanza excepción si items está vacío")
        void testEmptyItems() {
            OrderContext ctx = OrderContext.of("C001", Collections.emptyList(),
                    null, "CASH", DEFAULT_ADDRESS, false);
            assertThrows(IllegalArgumentException.class, () -> pipeline.execute(ctx));
        }
    }

    // --- Pedidos sin descuento ---

    @Nested
    @DisplayName("Pedidos sin descuento")
    class NoDiscountTests {

        @Test
        @DisplayName("Pedido simple sin descuento (subtotal < 100)")
        void testSimpleOrder() {
            OrderContext ctx = createContextWithItems(
                    List.of(new OrderItem(10.0, 1)),
                    null, "CASH", false
            );
            OrderContext result = pipeline.execute(ctx);

            assertEquals(10.0, result.subtotal(), 0.01);
            assertEquals(0.0, result.discountFactor(), 0.001);
            assertEquals(5.99, result.shippingCost(), 0.01);
            assertEquals(15.99, result.total(), 0.01);
        }

        @Test
        @DisplayName("Envío gratuito si subtotal >= 100")
        void testFreeShipping() {
            OrderContext ctx = createContext(null, "CASH", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(100.0, result.subtotal(), 0.01);
            assertEquals(0.0, result.shippingCost(), 0.01);
            assertEquals(100.0, result.total(), 0.01);
        }
    }

    // --- Descuentos ---

    @Nested
    @DisplayName("Descuentos")
    class DiscountTests {

        @Test
        @DisplayName("PROMO10 aplica 10% de descuento")
        void testPromo10() {
            OrderContext ctx = createContext("PROMO10", "CASH", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(0.10, result.discountFactor(), 0.001);
            assertEquals(90.0, result.total(), 0.01);
        }

        @Test
        @DisplayName("PROMO20 aplica 20% de descuento")
        void testPromo20() {
            OrderContext ctx = createContext("PROMO20", "CASH", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(0.20, result.discountFactor(), 0.001);
            assertEquals(80.0, result.total(), 0.01);
        }

        @Test
        @DisplayName("VIP con cliente no VIP aplica 5%")
        void testVipNonVip() {
            OrderContext ctx = createContext("VIP", "CASH", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(0.05, result.discountFactor(), 0.001);
            assertEquals(95.0, result.total(), 0.01);
        }

        @Test
        @DisplayName("VIP con cliente VIP aplica 30%")
        void testVipWithVipCustomer() {
            OrderPipeline vipPipeline = new OrderPipeline(ALWAYS_VIP);
            OrderContext ctx = createContext("VIP", "CASH", false);
            OrderContext result = vipPipeline.execute(ctx);

            assertEquals(0.30, result.discountFactor(), 0.001);
            assertEquals(70.0, result.total(), 0.01);
        }

        @Test
        @DisplayName("Código inválido no aplica descuento")
        void testInvalidCode() {
            OrderContext ctx = createContext("INVALIDO", "CASH", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(0.0, result.discountFactor(), 0.001);
            assertEquals(100.0, result.total(), 0.01);
        }
    }

    // --- Envío express ---

    @Nested
    @DisplayName("Envío express")
    class ExpressTests {

        @Test
        @DisplayName("Express cobra $15 independientemente del subtotal")
        void testExpress() {
            OrderContext ctx = createContext(null, "CASH", true);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(15.0, result.shippingCost(), 0.01);
            assertEquals(115.0, result.total(), 0.01);
        }
    }

    // --- Tipos de pago ---

    @Nested
    @DisplayName("Tipos de pago")
    class PaymentTests {

        @Test
        @DisplayName("CREDIT agrega 3% de recargo")
        void testCredit() {
            OrderContext ctx = createContext(null, "CREDIT", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(103.0, result.total(), 0.01);
        }

        @Test
        @DisplayName("CRYPTO aplica 2% de descuento")
        void testCrypto() {
            OrderContext ctx = createContext(null, "CRYPTO", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(98.0, result.total(), 0.01);
        }
    }

    // --- Casos combinados ---

    @Nested
    @DisplayName("Escenarios combinados")
    class CombinedTests {

        @Test
        @DisplayName("PROMO10 + CREDIT + Express")
        void testPromo10CreditExpress() {
            OrderContext ctx = createContextWithItems(
                    List.of(new OrderItem(25.0, 2), new OrderItem(50.0, 1)),
                    "PROMO10", "CREDIT", true
            );
            OrderContext result = pipeline.execute(ctx);

            assertEquals(100.0, result.subtotal(), 0.01);
            assertEquals(0.10, result.discountFactor(), 0.001);
            assertEquals(15.0, result.shippingCost(), 0.01);
            assertEquals(108.15, result.total(), 0.01);
        }

        @Test
        @DisplayName("PROMO20 + CRYPTO + sin express (subtotal >= 100)")
        void testPromo20CryptoNoExpress() {
            OrderContext ctx = createContext("PROMO20", "CRYPTO", false);
            OrderContext result = pipeline.execute(ctx);

            assertEquals(78.40, result.total(), 0.01);
        }

        @Test
        @DisplayName("VIP (cliente VIP) + Express + CASH")
        void testVipExpressCash() {
            OrderPipeline vipPipeline = new OrderPipeline(ALWAYS_VIP);
            OrderContext ctx = createContext("VIP", "CASH", true);
            OrderContext result = vipPipeline.execute(ctx);

            assertEquals(85.0, result.total(), 0.01);
        }
    }

    // --- OrderItem validación ---

    @Nested
    @DisplayName("Validación de OrderItem")
    class OrderItemTests {

        @Test
        @DisplayName("Precio negativo lanza excepción")
        void testNegativePrice() {
            assertThrows(IllegalArgumentException.class,
                    () -> new OrderItem(-1.0, 1));
        }

        @Test
        @DisplayName("Cantidad cero lanza excepción")
        void testZeroQuantity() {
            assertThrows(IllegalArgumentException.class,
                    () -> new OrderItem(10.0, 0));
        }

        @Test
        @DisplayName("OrderItem válido se crea correctamente")
        void testValidItem() {
            OrderItem item = new OrderItem(25.0, 3);
            assertEquals(25.0, item.price());
            assertEquals(3, item.quantity());
        }
    }
}
