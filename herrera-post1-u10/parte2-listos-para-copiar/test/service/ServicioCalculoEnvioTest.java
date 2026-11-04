package com.universidad.enviosservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioCalculoEnvioTest {

    private final ServicioCalculoEnvio servicio = new ServicioCalculoEnvio();

    // ---- Pruebas de partida (Parte 1) ----
    @Test
    void calculaEnvioEstandarPesoBajo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", false);
        assertEquals(50.0, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClientePremium() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", false);
        assertEquals(45.0, costo, 0.01);
    }

    // ---- Ramas antes sin cubrir (Parte 2) ----
    @Test
    void aplicaRecargoPorFragilidad() {
        // 50 + 15% = 57.5
        assertEquals(57.5, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", true), 0.01);
    }

    @Test
    void aplicaDescuentoClienteCorporativo() {
        // 50 * 0.85 = 42.5
        assertEquals(42.5, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "CORPORATIVO", false), 0.01);
    }

    @Test
    void combinaFragilidadYDescuentoPremium() {
        // 50 * 1.15 * 0.9 = 51.75 (el orden de los ajustes importa)
        assertEquals(51.75, servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", true), 0.01);
    }

    @Test
    void rechazaMetodoDeEnvioNoSoportado() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> servicio.calcularCosto("DRON", 3.0, 100.0, "REGULAR", false));
        assertEquals("Metodo de envio no soportado: DRON", ex.getMessage());
    }

    @ParameterizedTest(name = "{0} delega en su estrategia -> {1}")
    @CsvSource({
        "ESTANDAR, 50.0",
        "EXPRESS, 156.0",
        "OVERNIGHT, 220.0",
        "INTERNACIONAL, 230.0"   // 100*1.8 + 50
    })
    void cadaMetodoDelegaEnSuEstrategia(String metodo, double esperado) {
        assertEquals(esperado, servicio.calcularCosto(metodo, 3.0, 100.0, "REGULAR", false), 0.01);
    }
}
