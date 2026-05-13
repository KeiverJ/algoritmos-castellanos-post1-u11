package com.diseno.calidad.refactored;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para las estrategias de descuento.
 * Cubre los cuatro casos: PROMO10, PROMO20, VIP (con/sin VIP), código inválido.
 */
class DiscountStrategyTest {

    // --- Mock simple del repositorio ---

    private static final CustomerRepository ALWAYS_VIP = id -> true;
    private static final CustomerRepository NEVER_VIP = id -> false;

    // --- fixed() ---

    @Test
    @DisplayName("fixed(0.10) retorna siempre 0.10")
    void testFixed() {
        DiscountStrategy strategy = DiscountStrategies.fixed(0.10);
        assertEquals(0.10, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    // --- noDiscount() ---

    @Test
    @DisplayName("noDiscount() retorna siempre 0.0")
    void testNoDiscount() {
        DiscountStrategy strategy = DiscountStrategies.noDiscount();
        assertEquals(0.0, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    // --- vip() ---

    @Test
    @DisplayName("vip() con cliente VIP retorna vipFactor")
    void testVipCustomer() {
        DiscountStrategy strategy = DiscountStrategies.vip(ALWAYS_VIP, 0.30, 0.05);
        assertEquals(0.30, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    @Test
    @DisplayName("vip() con cliente no VIP retorna defaultFactor")
    void testNonVipCustomer() {
        DiscountStrategy strategy = DiscountStrategies.vip(NEVER_VIP, 0.30, 0.05);
        assertEquals(0.05, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    // --- fromCode() ---

    @Test
    @DisplayName("fromCode PROMO10 retorna 0.10")
    void testFromCodePromo10() {
        DiscountStrategy strategy = DiscountStrategies.fromCode("PROMO10", NEVER_VIP);
        assertEquals(0.10, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    @Test
    @DisplayName("fromCode PROMO20 retorna 0.20")
    void testFromCodePromo20() {
        DiscountStrategy strategy = DiscountStrategies.fromCode("PROMO20", NEVER_VIP);
        assertEquals(0.20, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    @Test
    @DisplayName("fromCode VIP con cliente VIP retorna 0.30")
    void testFromCodeVipWithVipCustomer() {
        DiscountStrategy strategy = DiscountStrategies.fromCode("VIP", ALWAYS_VIP);
        assertEquals(0.30, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    @Test
    @DisplayName("fromCode VIP con cliente no VIP retorna 0.05")
    void testFromCodeVipWithNonVipCustomer() {
        DiscountStrategy strategy = DiscountStrategies.fromCode("VIP", NEVER_VIP);
        assertEquals(0.05, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    @Test
    @DisplayName("fromCode inválido retorna 0.0")
    void testFromCodeInvalid() {
        DiscountStrategy strategy = DiscountStrategies.fromCode("INVALIDO", NEVER_VIP);
        assertEquals(0.0, strategy.discountFactor(100.0, "C001"), 0.001);
    }

    @Test
    @DisplayName("fromCode null retorna 0.0")
    void testFromCodeNull() {
        DiscountStrategy strategy = DiscountStrategies.fromCode(null, NEVER_VIP);
        assertEquals(0.0, strategy.discountFactor(100.0, "C001"), 0.001);
    }
}
