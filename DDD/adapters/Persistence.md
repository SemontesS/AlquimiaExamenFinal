# Persistence — Operación Alquimia

Adaptador de salida: `InMemoryDeviceReportRepository` (`alquimia.adapters.persistence`) implementa
`DeviceReportRepository` con un `ConcurrentHashMap<SurveillanceDevice, DeviceReport>`.

| Operación | Implementación | Garantía |
|---|---|---|
| `saveIfAbsent` | `putIfAbsent` | Atómica: dos registros simultáneos del mismo dispositivo no se pisan |
| `update` | `computeIfPresent` | Atómica: reemplazo y actualización parcial no se mezclan |
| `deleteByDevice` | `remove` | Solo afecta al dispositivo indicado |
| `findAll` | copia inmutable de los valores | Lectura consistente |

Los datos se pierden al reiniciar la aplicación. Cambiar a otro almacenamiento solo requeriría un nuevo
adaptador que implemente el mismo puerto, sin tocar el dominio.
