# Domain Model — Operación Alquimia

## 1. Introducción

El dominio de **Operación Alquimia** modela la información que el Equipo Flash intercepta de las
comunicaciones de Alchemy. Tres dispositivos de vigilancia ubicados en Central City informan, cada
uno, la **distancia** hasta el laboratorio y un **fragmento del mensaje**. Combinando los tres se
obtiene la **posición del laboratorio** (trilateración) y el **mensaje original**.


## 2. Conceptos principales

- SurveillanceDevice
- DeviceReport
- Distance
- MessageFragment
- Point
- SurveillanceResult

## 3. Clasificación DDD

### Entidad / Aggregate Root

- **DeviceReport**: lo que reportó un dispositivo. Su identidad es el dispositivo (`SurveillanceDevice`);
  solo puede existir uno por dispositivo. Es el único aggregate y lo que guarda el repositorio.

### Value Objects

- **SurveillanceDevice** (catálogo cerrado: STAR Labs, CCPD, S.T.A.R. Satellite, cada uno con su posición fija)
- **Distance**, **MessageFragment**, **Point**, **SurveillanceResult**

### Servicios de dominio

- TrilaterationService, MessageReconstructionService, LaboratoryAnalysisService y un servicio por caso de uso
  (ver `services/Domain Services.md`).

## 4. Diagrama

``` text
DeviceReport  (aggregate root, identidad = device)
│
├── device   : SurveillanceDevice ── position : Point
├── distance : Distance
└── message  : MessageFragment ── words : List<String>   ("" = palabra perdida)

SurveillanceResult  (resultado del análisis)
├── position : Point
└── message  : String
```

## 5. Reglas de negocio protegidas por el dominio

| Regla | Dónde se protege |
|---|---|
| La distancia es un número finito mayor o igual a 0 | `Distance` |
| El fragmento no puede ser nulo ni vacío; una palabra nula se toma como `""` | `MessageFragment` |
| Un reporte siempre tiene dispositivo, distancia y mensaje | `DeviceReport` |
| Reemplazo: ambos valores nuevos. Actualización parcial: solo cambia lo enviado y debe enviarse al menos un valor | `DeviceReport.replaceWith` / `updatedWith` |
| Los nombres de dispositivo se normalizan (mayúsculas, espacios, puntos y guiones se ignoran) | `SurveillanceDevice.from` |
| Los fragmentos vacíos se ignoran al reconstruir; los demás deben coincidir y tener el mismo largo | `MessageReconstructionService` |
| Se necesitan los tres dispositivos para procesar | `LaboratoryAnalysisService`, `TrilaterationService` |

## 6. Errores de negocio

| Excepción | Significado |
|---|---|
| `InvalidDeviceDataException` | Información inválida o incompleta |
| `UnknownDeviceException` | Dispositivo desconocido |
| `DeviceReportNotFoundException` | El dispositivo no tiene información almacenada |
| `DuplicateDeviceReportException` | El dispositivo ya tiene información |
| `InsufficientDataException` | No hay datos suficientes para calcular |

El dominio solo lanza estas excepciones. Su traducción a códigos HTTP ocurre en el adaptador REST.
