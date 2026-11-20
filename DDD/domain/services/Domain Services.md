# Domain Services — Operación Alquimia

## 1. Servicios de cálculo

| Servicio | Responsabilidad |
|---|---|
| `TrilaterationService` | Calcula la posición del laboratorio a partir de las tres distancias |
| `MessageReconstructionService` | Reconstruye el mensaje a partir de los fragmentos |
| `LaboratoryAnalysisService` | Combina los dos anteriores sobre los reportes de los tres dispositivos |

### Trilateración

Cada dispositivo dibuja un círculo de radio igual a su distancia. Restando las ecuaciones de los
círculos de dos en dos se obtienen dos ecuaciones lineales en (x, y):

``` text
A x + B y = C     A = 2(x2-x1)   B = 2(y2-y1)   C = r1² - r2² - x1² + x2² - y1² + y2²
D x + E y = F     D = 2(x3-x2)   E = 2(y3-y2)   F = r2² - r3² - x2² + x3² - y2² + y3²

x = (C·E - F·B) / (E·A - B·D)
y = (A·F - D·C) / (E·A - B·D)
```

Los tres dispositivos no son colineales, así que el determinante nunca es 0. El resultado se
redondea a 2 decimales. Si falta la distancia de algún dispositivo, lanza `InsufficientDataException`.

### Reconstrucción del mensaje

1. Todos los fragmentos deben tener el mismo largo; si no, `InvalidDeviceDataException`.
2. Para cada posición se toman las palabras no vacías de los fragmentos.
3. Si no hay ninguna: `InsufficientDataException` (la palabra se perdió en todos).
4. Si hay más de una distinta: `InvalidDeviceDataException` (los fragmentos se contradicen).
5. Si hay una sola, esa es la palabra. Las palabras se unen con espacios.

## 2. Servicios por caso de uso

| Servicio | Req. | Responsabilidad |
|---|---|---|
| `ProcessCompleteInformationService` | 2.1 | Valida que lleguen exactamente los 3 dispositivos, sin repetidos, y analiza sin almacenar |
| `RegisterDeviceReportService` | 2.2 | Registra un dispositivo; si ya existía lanza `DuplicateDeviceReportException` |
| `LocateLaboratoryService` | 2.3 | Analiza lo acumulado en el repositorio |
| `ReplaceDeviceReportService` | 2.4 | Reemplaza completamente; exige distancia y mensaje y que el dispositivo exista |
| `UpdateDeviceReportService` | 2.5 | Cambia solo lo enviado; exige al menos un valor y que el dispositivo exista |
| `DeleteDeviceReportService` | 2.6 | Elimina solo el dispositivo seleccionado |
| `ConsultDeviceReportsService` | — | Lista o consulta lo almacenado |

Ninguno lleva anotaciones de Spring: se conectan en `infrastructure.config.BeanConfig`.
