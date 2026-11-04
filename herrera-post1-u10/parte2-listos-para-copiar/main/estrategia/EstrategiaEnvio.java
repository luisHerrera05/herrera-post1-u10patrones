package com.universidad.enviosservice.estrategia;

/** Contrato común: calcula el costo base de un envío según su método de transporte. */
public interface EstrategiaEnvio {
    double calcular(double pesoKg, double distanciaKm);
}
