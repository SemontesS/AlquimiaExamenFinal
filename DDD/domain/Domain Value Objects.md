# Domain Value Objects — Operación Alquimia

## 1. Introducción

Los **Value Objects** representan conceptos cuyo significado depende del valor que contienen y no
de una identidad propia. Son inmutables y se validan al construirse, de modo que un objeto inválido
nunca llega a existir.

## 2. Value Objects identificados

``` text
Domain Value Objects
│
├── SurveillanceDevice
├── Point
├── Distance
├── MessageFragment
└── SurveillanceResult
```

## 3. Detalle

### SurveillanceDevice

Catálogo cerrado de dispositivos, con su posición fija en Central City.

| Valor | Nombre | Posición (X, Y) |
|---|---|---|
| `STAR_LABS` | STAR Labs | [-500, -200] |
| `CCPD` | CCPD | [100, -100] |
| `STAR_SATELLITE` | S.T.A.R. Satellite | [500, 100] |

`SurveillanceDevice.from(texto)` busca el dispositivo ignorando mayúsculas, espacios, puntos, guiones
y guiones bajos (`"S.T.A.R. Satellite"`, `"star-satellite"` y `"STAR_SATELLITE"` son el mismo). Si no
existe lanza `UnknownDeviceException`.

### Point

Coordenada `(x, y)`. `rounded()` redondea ambas a 2 decimales (la precisión de las distancias
recibidas) y evita el `-0.0`.

### Distance

Distancia del dispositivo al laboratorio. Debe ser un número finito mayor o igual a 0; si no,
`InvalidDeviceDataException`. `Distance.of(Double)` también rechaza el valor nulo.

### MessageFragment

Lista inmutable de palabras escuchadas por un dispositivo. Una palabra perdida por la interferencia
de la Piedra Filosofal es la cadena vacía `""`.

- No puede ser nula ni tener cero palabras.
- Una palabra nula se convierte en `""`, y las palabras se recortan (`trim`).

### SurveillanceResult

Resultado del análisis: `position` (Point) y `message` (texto reconstruido).
