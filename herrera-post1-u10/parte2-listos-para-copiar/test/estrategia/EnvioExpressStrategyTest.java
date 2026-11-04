package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioExpressStrategyTest {

    private final EnvioExpressStrategy estrategia = new EnvioExpressStrategy();

    @ParameterizedTest(name = "{0} kg, {1} km -> {2}")
    @CsvSource({
        "3.0, 100.0, 156.0",    // (100*1.2) * 1.3
        "8.0, 100.0, 161.85",   // (120 + 3*1.5) * 1.3
        "25.0, 100.0, 198.25",  // (120 + 22.5 + 5*2.0) * 1.3
        "3.0, 500.0, 780.0",    // límite exacto 500 km: sin recargo
        "3.0, 600.0, 968.5"     // > 500 km: (720 + 25) * 1.3
    })
    void calculaCostoPorTramoYRecargo(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
