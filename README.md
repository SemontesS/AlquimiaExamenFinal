# Operación Alquimia — Protocolo Savitar

API REST (Java 17, Spring Boot 4.1.1) que localiza el laboratorio de Alchemy por **trilateración** y reconstruye el mensaje interceptado a partir de los fragmentos de tres dispositivos. Los datos viven solo en memoria. No usa base de datos ni servicios externos.

## Para ejecutarlo

```powershell
.\mvnw spring-boot:run     # o: mvn spring-boot:run
.\mvnw test                # pruebas unitarias
```
Servidor en `http://localhost:8080`.

## Arquitectura (DDD + Hexagonal)

```
alquimia
├── domain                      
│   ├── models                  
│   ├── exceptions               
│   ├── ports
│   │   ├── in                   
│   │   └── out                 
│   └── services                
│                               
├── adapters
│   ├── rest                     
│   └── persistence              
└── infrastructure/config        
```
El dominio no importa nada de `org.springframework`, ni de `jakarta`. El `GlobalExceptionHandler` es quien convierte los errores de negocio en códigos HTTP.

## Documentación de diseño (SDD)

En la carpeta `SDD/`:

- `SDD/domain/Domain Model.md`: modelo de dominio y reglas de negocio.
- `SDD/domain/Domain Value Objects.md`: value objects.
- `SDD/domain/Input-ports.md` y `SDD/domain/Output-ports.md`: puertos.
- `SDD/domain/services/Domain Services.md`: servicios de dominio, trilateración y reconstrucción.
- `SDD/adapters/REST API.md` y `SDD/adapters/Persistence.md`: adaptadores.

## Dispositivos

| Dispositivo | Posición | Identificador en la URL |
|---|---|---|
| STAR Labs | [-500, -200] | `star-labs` |
| CCPD | [100, -100] | `ccpd` |
| S.T.A.R. Satellite | [500, 100] | `star-satellite` |

El nombre se acepta sin importar mayúsculas, espacios, puntos ni guiones (`STAR Labs`, `S.T.A.R. Satellite`, `star_labs`...).

## Endpoints

Base: `/api/v1/alchemy`

| Req. | Método y ruta | Descripción | Éxito |
|---|---|---|---|
| 2.1 | `POST /topsecret` | Recibe los 3 dispositivos a la vez y devuelve posición y mensaje. No guarda nada | 200 |
| 2.2 | `POST /devices/{device}` | Registra la información de un dispositivo | 201 |
| — | `GET /devices` y `GET /devices/{device}` | Consulta lo almacenado | 200 |
| 2.3 | `GET /laboratory` | Procesa lo acumulado: posición y mensaje | 200 |
| 2.4 | `PUT /devices/{device}` | Reemplazo completo (distancia y mensaje obligatorios) | 200 |
| 2.5 | `PATCH /devices/{device}` | Actualización parcial: solo cambia lo enviado | 200 |
| 2.6 | `DELETE /devices/{device}` | Elimina un dispositivo, los demás no cambian | 204 |

### Ejemplos

`POST /api/v1/alchemy/topsecret`
```json
{
  "devices": [
    { "name": "STAR Labs",          "distance": 538.52, "message": ["este", "", "", "mensaje", ""] },
    { "name": "CCPD",               "distance": 141.42, "message": ["", "es", "", "", "secreto"] },
    { "name": "S.T.A.R. Satellite", "distance": 509.9,  "message": ["este", "", "un", "", ""] }
  ]
}
```
Respuesta `200`:
```json
{ "position": { "x": 0.0, "y": 0.0 }, "message": "este es un mensaje secreto" }
```

`POST /api/v1/alchemy/devices/star-labs`  →  `201`
```json
{ "distance": 538.52, "message": ["este", "", "", "mensaje", ""] }
```

`PATCH /api/v1/alchemy/devices/ccpd` (solo la distancia; el mensaje se conserva)
```json
{ "distance": 141.5 }
```

## Códigos de error

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos o incompletos: distancia negativa o ausente, mensaje vacío, dispositivo desconocido, fragmentos de distinto largo o que se contradicen, JSON mal formado, `PATCH` sin datos, `topsecret` sin los 3 dispositivos o con alguno repetido |
| 404 | Consultar, reemplazar, actualizar o eliminar un dispositivo sin información almacenada |
| 409 | Registrar (`POST`) un dispositivo que ya tiene información (para cambiarla se usa `PUT` o `PATCH`) |
| 422 | `GET /laboratory`: no hay datos suficientes (faltan dispositivos o una palabra se perdió en todos los fragmentos) |

Todo error responde `{ timestamp, status, error, message, path }`.

## Decisiones y supuestos

- **Trilateración.** Se restan las ecuaciones de los tres círculos y se resuelve el sistema lineal resultante. Los dispositivos no son colineales, así que siempre hay solución. La posición se redondea a 2 decimales. No se rechazan distancias "inconsistentes": con valores aproximados se devuelve la mejor solución lineal. Al estar las distancias redondeadas a 2 decimales, la posición puede variar unos centésimos.
- **Mensaje.** Cada fragmento es una lista de palabras en la que `""` es una palabra perdida. Todos los fragmentos deben tener el mismo largo. Se combinan posición a posición, ignorando las cadenas vacías. Si dos fragmentos traen palabras distintas en la misma posición, es un error (400). Si una posición queda vacía en todos, no hay datos suficientes.
- **Registro frente a reemplazo.** `POST` solo crea; si ya existe responde 409. `PUT` y `PATCH` exigen que el dispositivo ya exista (404 si no).
- **Concurrencia.** El repositorio usa `ConcurrentHashMap` con operaciones atómicas por dispositivo (`putIfAbsent`, `computeIfPresent`), de modo que actualizar y eliminar mantienen la consistencia.

## Pruebas

`src/test/java/alquimia/domain`: trilateración, reconstrucción del mensaje y los seis casos de uso (casos felices y de error), sin levantar Spring.
