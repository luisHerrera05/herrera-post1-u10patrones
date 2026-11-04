package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioInternacionalStrategyTest {

    private final EnvioInternacionalStrategy estrategia = new EnvioInternacionalStrategy();

    @ParameterizedTest(name = "{0} kg, {1} km -> {2}")
    @CsvSource({
        "3.0, 1000.0, 1850.0",    // tramo <= 5 kg
        "8.0, 1000.0, 1859.0",    // 5 < peso <= 20
        "25.0, 1000.0, 1917.5",   // > 20 kg
        "3.0, 5000.0, 9050.0",    // límite exacto 5000 km: sin recargo
        "3.0, 6000.0, 10950.0"    // > 5000 km: recargo de 100
    })
    void calculaCostoPorTramoYRecargo(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
