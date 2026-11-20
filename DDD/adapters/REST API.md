# REST API — Operación Alquimia

Adaptador de entrada (`alquimia.adapters.rest`). Base: `/api/v1/alchemy`. El controlador solo traduce
entre HTTP y los puertos de entrada; no contiene lógica de negocio.

## Endpoints

| Req. | Método y ruta | Cuerpo | Éxito |
|---|---|---|---|
| 2.1 | `POST /topsecret` | `{ "devices": [ { "name", "distance", "message" } x3 ] }` | 200 |
| 2.2 | `POST /devices/{device}` | `{ "distance", "message" }` (ambos obligatorios) | 201 |
| — | `GET /devices` | — | 200 |
| — | `GET /devices/{device}` | — | 200 |
| 2.3 | `GET /laboratory` | — | 200 |
| 2.4 | `PUT /devices/{device}` | `{ "distance", "message" }` (ambos obligatorios) | 200 |
| 2.5 | `PATCH /devices/{device}` | `{ "distance"? , "message"? }` (al menos uno) | 200 |
| 2.6 | `DELETE /devices/{device}` | — | 204 |

`{device}`: `star-labs`, `ccpd` o `star-satellite` (también se acepta el nombre completo).

Respuesta de 2.1 y 2.3: `{ "position": { "x", "y" }, "message": "..." }`.
Respuesta de un dispositivo: `{ "device", "distance", "message": [ ... ] }`.

## Códigos de error

| Código | Excepción de dominio / causa |
|---|---|
| 400 | `InvalidDeviceDataException`, `UnknownDeviceException`, validación del cuerpo, JSON mal formado |
| 404 | `DeviceReportNotFoundException` |
| 409 | `DuplicateDeviceReportException` |
| 422 | `InsufficientDataException` |

Formato de error: `{ "timestamp", "status", "error", "message", "path" }`. La traducción la hace
`GlobalExceptionHandler`, de modo que el dominio no conoce los códigos HTTP.

## DTOs

`CompleteInformationRequest`, `CompleteDeviceRequest`, `DeviceReportRequest`, `PartialDeviceReportRequest`,
`DeviceReportResponse`, `LaboratoryResponse`, `PositionResponse`, `ErrorResponse`. `RestMapper`
convierte entre DTOs y dominio.
