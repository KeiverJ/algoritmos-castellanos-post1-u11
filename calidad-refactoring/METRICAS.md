# METRICAS.md — Registro de Métricas de Calidad

## Clase: OrderProcessor (original)

| Métrica                | Valor  | Umbral recomendado | ¿Viola? |
|------------------------|--------|--------------------|---------|
| V(G) `processOrder`    | 16     | ≤ 10               | **Sí**  |
| NPath `processOrder`   | 810    | ≤ 200              | **Sí**  |
| Líneas por método      | 60     | ≤ 30               | **Sí**  |
| Responsabilidades      | 5      | 1 (SRP)            | **Sí**  |
| Cobertura JaCoCo       | 97.8%  | ≥ 80%              | No      |

### Code Smells Detectados

| Code Smell          | Evidencia                                                    |
|---------------------|--------------------------------------------------------------|
| God Class           | Una sola clase maneja validación, descuento, envío, pago, persistencia |
| Long Method         | `processOrder` tiene 60 líneas (PMD reporta ExcessiveMethodLength)     |
| Feature Envy        | Lógica de descuento depende de datos de `discountCode` externo         |
| Primitive Obsession | Items como `List<Map<String,Object>>`, dinero como `double`           |

---

## Clase: OrderProcessor (refactorizada) — Pipeline + Strategy

| Métrica                     | Valor | Umbral recomendado | ¿Viola? |
|-----------------------------|-------|--------------------|---------|
| V(G) `ValidationStage`      | 3     | ≤ 3                | No      |
| V(G) `SubtotalStage`        | 1     | ≤ 3                | No      |
| V(G) `DiscountStage`        | 1     | ≤ 3                | No      |
| V(G) `ShippingStage`        | 3     | ≤ 3                | No      |
| V(G) `PaymentStage`         | 3     | ≤ 3                | No      |
| V(G) `PersistenceStage`     | 1     | ≤ 3                | No      |
| V(G) `DiscountStrategies`   | 2     | ≤ 5                | No      |
| V(G) promedio por método    | 2.3   | ≤ 3                | No      |
| Líneas máximas por método   | 10    | ≤ 30               | No      |
| Responsabilidades por clase | 1     | 1 (SRP)            | No      |
| Cobertura JaCoCo            | 100%  | ≥ 80%              | No      |
| PMD violaciones             | 0     | 0                  | No      |

---

## Comparación Cuantitativa

| Métrica                          | Original | Refactorizado | Reducción |
|----------------------------------|----------|---------------|-----------|
| V(G) máxima                      | 16       | 3             | 81.2%     |
| V(G) promedio por método         | 16       | 2.3           | 85.6%     |
| NPath máximo                     | 810      | ~12           | 98.5%     |
| Líneas máximas por método        | 60       | 10            | 83.3%     |
| Clases                           | 1        | 12            | +11       |
| Métodos de prueba                | 14       | 30            | +16       |
| Cobertura JaCoCo                 | 97.8%    | 100%          | +2.2%     |
| Violaciones PMD                  | 3        | 0             | 100%      |

---

## Análisis

### ¿Cuánto se redujo la complejidad ciclomática promedio por método?

La complejidad ciclomática se redujo de **V(G) = 16** en el método monolítico `processOrder`
a un promedio de **V(G) = 2.3** entre las etapas del pipeline y las estrategias. Esto representa
una reducción del **85.6%**. Ninguna clase refactorizada supera V(G) = 4, cumpliendo el umbral
de ≤ 3 en todas las etapas del pipeline.

### ¿Cuántas clases se requirieron para distribuir las responsabilidades?

Se pasó de **1 clase monolítica** con 5 responsabilidades a **12 clases** con responsabilidad
única cada una:

- 1 interfaz `Stage<T>` (pipeline genérico)
- 6 etapas concretas (`ValidationStage`, `SubtotalStage`, `DiscountStage`, `ShippingStage`,
  `PaymentStage`, `PersistenceStage`)
- 1 interfaz `DiscountStrategy` (patrón Strategy)
- 1 fábrica `DiscountStrategies`
- 1 record `OrderContext` (contexto inmutable)
- 1 record `OrderItem` (Value Object)
- 1 ensamblador `OrderPipeline`

### ¿Cómo cambió la testabilidad?

La testabilidad mejoró significativamente:

- **Original**: 14 pruebas para un único método monolítico. Cada prueba requería construir
  `List<Map<String,Object>>` manualmente y verificar solo el resultado final.
- **Refactorizado**: 30 pruebas que incluyen pruebas unitarias aisladas para cada estrategia
  de descuento y pruebas de integración para el pipeline completo. Es posible probar cada
  etapa independientemente con mocks simples (lambdas para `CustomerRepository`).
- Se ganaron **+16 pruebas adicionales** gracias a la descomposición.

### ¿Qué trade-off introduce la refactorización?

| Aspecto              | Beneficio                                          | Costo                                    |
|----------------------|----------------------------------------------------|------------------------------------------|
| **Número de archivos** | Cada archivo tiene responsabilidad única           | 12 archivos vs. 1 (más navegación)       |
| **Indirección**      | Extensible: agregar descuentos = nueva lambda       | Flujo menos obvio al leer por primera vez |
| **Complejidad**      | V(G) ≤ 3 en cada etapa, fácil de mantener           | Más clases para entender el diseño        |
| **Testabilidad**     | Pruebas aisladas, mocks triviales                   | Más archivos de prueba que mantener       |
| **Rendimiento**      | Negligible (composición funcional, no hay overhead)  | Alocación de records inmutables extra     |

**Conclusión**: El trade-off es favorable. La indirección adicional es mínima y se compensa
con la capacidad de extender el sistema (nuevos descuentos, nuevas etapas) sin modificar
código existente (OCP). La mantenibilidad a largo plazo mejora sustancialmente.
