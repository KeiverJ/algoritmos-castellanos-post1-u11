package com.diseno.calidad.refactored;

/**
 * Repositorio de clientes para verificar el estado VIP.
 * Abstrae el acceso a la capa de datos, permitiendo inyectar
 * implementaciones reales o mocks en pruebas.
 */
public interface CustomerRepository {

    /**
     * Verifica si el cliente con el identificador dado es VIP.
     *
     * @param customerId identificador del cliente (no nulo)
     * @return {@code true} si el cliente es VIP, {@code false} en caso contrario
     */
    boolean isVip(String customerId);
}
