# Output Ports — Operación Alquimia

Puertos de salida del dominio (`alquimia.domain.ports.out`). Los adaptadores de persistencia los implementan.

| Puerto | Método | Descripción |
|---|---|---|
| `DeviceReportRepository` | `saveIfAbsent(report)` | Guarda solo si el dispositivo no tiene reporte. Devuelve `false` si ya existía |
| | `update(device, cambio)` | Aplica el cambio al reporte existente y devuelve el nuevo valor. Vacío si no existe |
| | `findByDevice(device)` | Busca el reporte de un dispositivo |
| | `findAll()` | Lista todos los reportes |
| | `deleteByDevice(device)` | Elimina; devuelve `false` si no había nada |

Las operaciones son **atómicas por dispositivo**, de modo que dos peticiones simultáneas no dejan
la información inconsistente. Los datos son temporales: se pierden al reiniciar, como exige el enunciado.
