package com.diseno.calidad.pipeline;

/**
 * Etapa genérica del patrón Pipeline (Pipes and Filters).
 * Cada etapa recibe un contexto, lo procesa y retorna el contexto enriquecido.
 *
 * @param <T> tipo del contexto que fluye por el pipeline
 */
@FunctionalInterface
public interface Stage<T> {

    /**
     * Procesa el contexto y retorna la versión enriquecida.
     *
     * @param context contexto de entrada
     * @return contexto procesado
     */
    T process(T context);

    /**
     * Compone esta etapa con la siguiente, creando un pipeline secuencial.
     *
     * @param next etapa siguiente a ejecutar después de esta
     * @return nueva etapa que ejecuta ambas en secuencia
     */
    default Stage<T> then(Stage<T> next) {
        return ctx -> next.process(this.process(ctx));
    }
}
