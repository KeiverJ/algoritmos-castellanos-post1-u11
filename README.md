# Refactorización Guiada por Métricas — Unidad 11

**Diseño de Algoritmos y Sistemas — Post-Contenido 1**  
**Ingeniería de Sistemas — UDES 2026**

---

## Objetivo

Aplicar métricas de calidad de software (complejidad ciclomática, NPath, LCOM4, acoplamiento)
para identificar code smells en un módulo con God Class, y refactorizar usando los patrones
**Strategy** y **Pipeline**, documentando el impacto cuantificable antes y después de la
intervención.

---

## Tecnologías y Versiones

| Tecnología | Versión |
| ---------- | ------- |
| Java       | 17+     |
| Maven      | 3.9.12  |
| JUnit 5    | 5.10.2  |
| PMD        | 6.55.0  |
| JaCoCo     | 0.8.11  |
| Git        | 2.x     |

---

## Estructura del Proyecto

```
calidad-refactoring/
├── pom.xml
├── pmd-ruleset.xml
├── METRICAS.md
└── src/
    ├── main/java/com/diseno/calidad/
    │   ├── original/
    │   │   └── OrderProcessor.java          ← God Class original
    │   ├── refactored/
    │   │   ├── OrderItem.java               ← Value Object (record)
    │   │   ├── OrderContext.java             ← Contexto inmutable del pipeline
    │   │   ├── CustomerRepository.java       ← Abstracción de datos
    │   │   ├── DiscountStrategy.java         ← Interfaz Strategy
    │   │   ├── DiscountStrategies.java       ← Fábrica de estrategias
    │   │   └── OrderPipeline.java            ← Ensamblador del pipeline
    │   └── pipeline/
    │       ├── Stage.java                    ← Interfaz genérica del pipeline
    │       ├── ValidationStage.java          ← Etapa: validación
    │       ├── SubtotalStage.java            ← Etapa: cálculo de subtotal
    │       ├── DiscountStage.java            ← Etapa: aplicación de descuento
    │       ├── ShippingStage.java            ← Etapa: costo de envío
    │       ├── PaymentStage.java             ← Etapa: procesamiento de pago
    │       └── PersistenceStage.java         ← Etapa: persistencia
    └── test/java/com/diseno/calidad/
        ├── original/
        │   └── OrderProcessorTest.java       ← 14 pruebas God Class
        └── refactored/
            ├── DiscountStrategyTest.java      ← 10 pruebas Strategy
            └── OrderPipelineTest.java         ← 20 pruebas Pipeline
```

---

## Prerrequisitos

- Java 17 o superior instalado y en el `PATH`
- Maven 3.8+ instalado y en el `PATH`
- Git configurado

Verificar:

```bash
java -version    # java 17.x.x o superior
mvn -version     # Apache Maven 3.8+
```

---

## Ejecución Paso a Paso

### 1. Clonar el repositorio

```bash
git clone https://github.com/KeiverJ/algoritmos-castellanos-post1-u11.git
cd algoritmos-castellanos-post1-u11/calidad-refactoring
```

### 2. Compilar el proyecto

```bash
mvn compile
```

### 3. Ejecutar pruebas con cobertura JaCoCo

```bash
mvn test
```

El reporte de cobertura se genera en `target/site/jacoco/index.html`.

### 4. Ejecutar análisis PMD

```bash
mvn pmd:pmd
```

El reporte PMD se genera en `target/pmd.xml` y `target/site/pmd.html`.

### 5. Verificar métricas

Comparar los valores de `METRICAS.md` con los reportes generados.

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    OrderPipeline                        │
│                                                         │
│  ┌──────────┐   ┌──────────┐   ┌──────────────┐        │
│  │Validation│──→│ Subtotal │──→│   Discount   │        │
│  │  Stage   │   │  Stage   │   │    Stage     │        │
│  └──────────┘   └──────────┘   └──────┬───────┘        │
│                                       │                 │
│                              ┌────────▼────────┐        │
│                              │DiscountStrategy │        │
│                              │  (Strategy)     │        │
│                              ├─────────────────┤        │
│                              │ fixed(0.10)     │        │
│                              │ fixed(0.20)     │        │
│                              │ vip(repo,...)   │        │
│                              │ noDiscount()    │        │
│                              └─────────────────┘        │
│                                                         │
│  ┌──────────┐   ┌──────────┐   ┌──────────────┐        │
│  │ Shipping │──→│ Payment  │──→│ Persistence  │        │
│  │  Stage   │   │  Stage   │   │    Stage     │        │
│  └──────────┘   └──────────┘   └──────────────┘        │
│                                                         │
│  Contexto inmutable: OrderContext (record)               │
└─────────────────────────────────────────────────────────┘
```

---

## Patrones de Diseño Aplicados

### Strategy (Descuentos)

Elimina la cadena de `if-else` del descuento original. Cada código de descuento
se resuelve a una implementación funcional de `DiscountStrategy`:

- `PROMO10` → `fixed(0.10)` — 10% fijo
- `PROMO20` → `fixed(0.20)` — 20% fijo
- `VIP` → `vip(repo, 0.30, 0.05)` — 30% si VIP, 5% si no
- Otro/null → `noDiscount()` — 0%

### Pipeline (Procesamiento de Pedidos)

Descompone el método monolítico `processOrder` en 6 etapas independientes,
cada una con responsabilidad única y V(G) ≤ 3:

1. **ValidationStage** — Valida customerId e items
2. **SubtotalStage** — Calcula precio × cantidad con Streams
3. **DiscountStage** — Aplica Strategy de descuento
4. **ShippingStage** — Calcula costo de envío
5. **PaymentStage** — Aplica modificador de tipo de pago
6. **PersistenceStage** — Simula almacenamiento

---

## Resultados de Métricas

| Métrica            | Original | Refactorizado | Reducción |
| ------------------ | -------- | ------------- | --------- |
| V(G) máxima        | 16       | 4             | 75%       |
| V(G) promedio      | 16       | 2.3           | 85.6%     |
| NPath máximo       | 810      | ~12           | 98.5%     |
| Líneas/método máx. | 60       | 10            | 83.3%     |
| Violaciones PMD    | 3        | 0             | 100%      |
| Cobertura JaCoCo   | 97.8%    | 100%          | +2.2%     |
| Pruebas unitarias  | 14       | 30            | +16       |

> Los detalles completos se encuentran en [`METRICAS.md`](calidad-refactoring/METRICAS.md).

---

## Pruebas Ejecutadas

### Clase Original (14 pruebas)

- Validación: null/blank customerId, null/empty items
- Descuentos: PROMO10, PROMO20, VIP (no VIP), código inválido
- Envío: express, gratuito (≥$100), estándar
- Pagos: CREDIT (+3%), CRYPTO (-2%), CASH
- Combinación: PROMO10 + CREDIT + Express

### Versión Refactorizada (30 pruebas)

- **DiscountStrategyTest** (10): fixed, noDiscount, vip VIP/no-VIP, fromCode × 6 casos
- **OrderPipelineTest** (20): Validación (4), sin descuento (2), descuentos (5),
  express (1), pagos (2), combinados (3), OrderItem (3)

Todas las pruebas pasan: **44 tests, 0 failures, 0 errors**.

---

## Decisiones Técnicas

| Decisión                                               | Justificación                                                                         |
| ------------------------------------------------------ | ------------------------------------------------------------------------------------- |
| Records para `OrderContext`/`OrderItem`                | Inmutabilidad garantizada, menos boilerplate, igualdad por valor                      |
| Métodos `withX()` en `OrderContext`                    | Mutación inmutable sin builders, claro y conciso                                      |
| `@FunctionalInterface` en `Stage` y `DiscountStrategy` | Permite composición con lambdas y method references                                   |
| Mocks con lambdas (no Mockito)                         | Sin dependencias adicionales, suficiente para interfaces funcionales                  |
| Paquete `com.diseno.calidad`                           | Maven no soporta caracteres no-ASCII en groupId; se usa `diseno` en lugar de `diseño` |
| `switch` expression en `fromCode()`                    | Java 17: exhaustivo, conciso, V(G) controlada                                         |

---

## Limitaciones

- `PersistenceStage` es una simulación (System.out). En producción se inyectaría un repositorio.
- La clase original `OrderProcessor.isVipCustomer()` siempre retorna `false`; el caso VIP=true
  solo se prueba en la versión refactorizada con mock.
- No se implementó LCOM4 automático (requiere herramientas externas como ck-metrics).

---

## Solución de Problemas

| Problema                         | Solución                                               |
| -------------------------------- | ------------------------------------------------------ |
| `mvn compile` falla con encoding | Verificar `UTF-8` en `pom.xml` y terminal              |
| PMD no genera reporte            | Ejecutar `mvn pmd:pmd` (no `pmd:check`)                |
| JaCoCo no genera reporte         | Ejecutar `mvn test` (JaCoCo se ejecuta en fase `test`) |
| Tests fallan con Java < 17       | Records y switch expressions requieren Java 17+        |
