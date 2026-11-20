# Input Ports — Operación Alquimia

Puertos de entrada del dominio (`alquimia.domain.ports.in`): uno por requerimiento funcional. El
controlador REST depende solo de estas interfaces; las implementan los servicios de dominio.

| Req. | Puerto | Operación | Descripción |
|---|---|---|---|
| 2.1 | `ProcessCompleteInformationUseCase` | `process(reports)` | Recibe los 3 dispositivos a la vez; devuelve posición y mensaje. No guarda nada |
| 2.2 | `RegisterDeviceReportUseCase` | `register(command)` | Registra la información de un dispositivo |
| 2.3 | `LocateLaboratoryUseCase` | `locate()` | Procesa lo acumulado; si no alcanza lanza `InsufficientDataException` |
| 2.4 | `ReplaceDeviceReportUseCase` | `replace(command)` | Reemplaza por completo la información de un dispositivo |
| 2.5 | `UpdateDeviceReportUseCase` | `update(command)` | Actualiza parcialmente: solo cambia lo enviado |
| 2.6 | `DeleteDeviceReportUseCase` | `delete(device)` | Elimina un dispositivo sin afectar a los demás |
| — | `ConsultDeviceReportsUseCase` | `findAll()`, `findByDevice(device)` | Consulta lo almacenado |

## Comando

`DeviceReportCommand(String device, Double distance, List<String> message)`. Un valor nulo significa
"no enviado": se usa así en la actualización parcial. `toReport()` construye un reporte completo y
exige distancia y mensaje.
