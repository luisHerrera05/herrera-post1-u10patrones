

## Entorno de análisis
[[Docker local o SonarCloud]]. Decisión de diseño 1: [[justifica con al menos dos criterios de la tabla:
dependencia de internet/límites del plan, consumo de recursos locales, visibilidad pública del dashboard,
necesidad de acceder desde otro equipo]].

## Estado inicial del análisis
| Métrica | Valor real observado |
|---------|----------------------|
| Complejidad ciclomática de calcularCosto() | X |
| Cobertura de líneas de ServicioCalculoEnvio | X% |
| Cobertura de ramas de ServicioCalculoEnvio | X% |
| Code Smells reportados | X |

## Interpretación
[[Qué método concentra el problema; qué ramas marca JaCoCo en rojo/amarillo (métodos EXPRESS, OVERNIGHT,
INTERNACIONAL, tramos de peso, recargos por distancia, fragilidad, CORPORATIVO, método no soportado);
por qué coinciden complejidad y cobertura baja en el mismo método.]]



---
# Diagnóstico y Mejora Guiada por Métricas

## Decisiones de diseño
1. **Entorno de análisis:** [[Docker local / SonarCloud — justificación]]
2. **Exclusiones del análisis:** No se excluye ninguna clase (`sonar.exclusions` vacío): el proyecto no tiene clase
   `Main` ni código generado, todas las clases contienen lógica de negocio y excluir alguna ocultaría información real.
3. **Métrica usada como señal principal:** [[Cyclomatic Complexity / Cognitive Complexity / Coverage — por qué.
   Idea: la complejidad ciclomática indica cuántos caminos independientes hay (20) y cuántas pruebas mínimas exige;
   la cobertura confirma el síntoma pero no explica la causa, por eso la complejidad guía la intervención.]]
4. **Patrón elegido: Strategy.** El método no guarda estado entre llamadas ni transiciones válidas entre estados
   (descarta **State**); el orden de evaluación de las ramas de `metodoEnvio` no importa y solo una se ejecuta, elegida
   por un valor discreto, sin una cadena de manejadores donde varios podrían aplicar (descarta **Chain of
   Responsibility**); cada rama calcula el mismo tipo de resultado (un costo) con una fórmula propia (encaja con
   **Strategy**). Se movió a las estrategias lo específico de cada método (tramos de peso, recargo por distancia,
   multiplicador Express, límite Overnight). Se dejó en el orquestador lo transversal (fragilidad y descuento por tipo
   de cliente) porque aplica igual a los cuatro métodos y duplicarlo cuatro veces sería peor. Por eso la CC del
   orquestador baja a 5 y no a 1.
5. **Criterio de pruebas agregadas:** cada prueba nueva cubre una rama que JaCoCo marcaba sin cubrir en la Parte 1:

   | Rama sin cubrir (Parte 1) | Prueba que la cubre |
   |---|---|
   | Tramos de peso 5–20 y >20 kg, y límites exactos 5 y 20 | `EnvioXxxStrategyTest` (parametrizadas) |
   | Recargo por distancia >500 km (Estándar y Express) y su límite exacto | `EnvioEstandarStrategyTest`, `EnvioExpressStrategyTest` |
   | Recargo >5000 km Internacional y su límite exacto | `EnvioInternacionalStrategyTest` |
   | Overnight >300 km (excepción) y límite exacto 300 | `EnvioOvernightStrategyTest` |
   | Fragilidad | `aplicaRecargoPorFragilidad`, `combinaFragilidadYDescuentoPremium` |
   | Descuento CORPORATIVO | `aplicaDescuentoClienteCorporativo` |
   | Método no soportado | `rechazaMetodoDeEnvioNoSoportado` |
   | Métodos EXPRESS/OVERNIGHT/INTERNACIONAL vía orquestador | `cadaMetodoDelegaEnSuEstrategia` |

   Todas verifican un valor numérico distinto; ninguna repite un caso ya cubierto.
6. **Realismo del Quality Gate:** CC = 10 por método es el techo del rango "Moderada" de la Tabla 3.2 de la guía y el
   valor por defecto de "Sonar Way": exigente sin obligar a reescribir métodos aún mantenibles (el original tenía 20,
   rango "Alta"). Cobertura 70% y no 100%: el 100% incentiva pruebas triviales solo para llenar el número
   (Sección 2.3 de la guía); 70% deja margen para código de infraestructura. Duplicación ≤ 5%. [[Agrega tu opinión:
   ¿el gate pasó? ¿qué condición falló y por qué?]]

## Quality Profile y Quality Gate "Estándar Laboratorio"
- Profile: duplicado de "Sonar Way", regla S1541 activa con Threshold = 10, asignado a envios-service.
- Gate: Coverage < 70% / New Code Smells > 0 / Duplicated Lines (%) > 5%, asignado a envios-service.
- Resultado tras el segundo análisis: [[Passed / Failed + explicación]]

## Comparación antes / después
| Métrica | Antes (Parte 1) | Después (Parte 2) |
|---------|------------------|--------------------|
| Complejidad ciclomática de calcularCosto() | X (20 por conteo manual) | X (5 por conteo manual) |
| Cobertura de ramas de la lógica de envío | X% | X% |
| Code Smells | X | X |
| Quality Gate | — | Passed / Failed |



## Cómo reproducir
```bash
docker run -d --name sonarqube -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:community
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
# reporte HTML: target/site/jacoco/index.html
```

## Herramientas utilizadas
- Java 21, Apache Maven, JUnit 5
- SonarQube Community / SonarCloud, JaCoCo, Docker
- Git, GitHub


