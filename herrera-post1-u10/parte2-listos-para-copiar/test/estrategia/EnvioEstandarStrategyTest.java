package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioEstandarStrategyTest {

    private final EnvioEstandarStrategy estrategia = new EnvioEstandarStrategy();

    @ParameterizedTest(name = "{0} kg, {1} km -> {2}")
    @CsvSource({
        "3.0, 100.0, 50.0",     // tramo <= 5 kg
        "5.0, 100.0, 50.0",     // límite exacto 5 kg (sigue en el primer tramo)
        "8.0, 100.0, 52.4",     // tramo 5 < peso <= 20
        "20.0, 100.0, 62.0",    // límite exacto 20 kg (sigue en el segundo tramo)
        "25.0, 100.0, 68.0",    // tramo > 20 kg
        "3.0, 500.0, 250.0",    // límite exacto 500 km: sin recargo
        "3.0, 600.0, 310.0"     // > 500 km: recargo de 10
    })
    void calculaCostoPorTramoYRecargo(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
