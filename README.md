# KALUZ – Backend de gestión de flotas (Taxi, Turismo y Combis · Huancayo)

Spring Boot 4 · Java 21 · JPA/Hibernate · PostgreSQL (Supabase). El proyecto Maven está en `demo/`.

## 1. Crear la base de datos en Supabase
1. Crea un proyecto en <https://supabase.com> (guarda la contraseña de la base de datos).
2. **SQL Editor → New query**, pega el contenido de [`db/schema.sql`](db/schema.sql) y pulsa **Run**.
3. (Opcional) ejecuta [`db/seed.sql`](db/seed.sql) para tener datos de prueba.
4. **Connect → Session pooler** y copia host, usuario (`postgres.<project-ref>`) y puerto `5432`.

## 2. Configurar el backend
```bash
cd demo
cp .env.example .env     # completa DB_URL, DB_USER, DB_PASSWORD  (.env está en .gitignore)
./mvnw spring-boot:run
```
En un servidor (Render, Railway, Fly…) define esas mismas variables de entorno
(`DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ORIGINS`). Supabase aloja la base de datos; el JAR de Spring se despliega aparte.

> Si usas el *Transaction pooler* (puerto 6543) agrega `&prepareThreshold=0` a `DB_URL`.

## 3. Endpoints
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/conductores/registro` | Registra chofer + unidad (+ documentos opcionales) |
| GET | `/api/conductores` | Lista conductores |
| PUT | `/api/vehiculos/{id}/gps` | Actualiza `latitud`/`longitud` |
| GET | `/api/vehiculos/cercanos?lat=&lng=&tipo=` | Vehículo con chofer ACTIVO más cercano (haversine, km) |
| GET | `/api/vehiculos` | Lista vehículos |
| POST | `/api/documentos` | Agrega SOAT / Revisión Técnica a un vehículo |
| GET | `/api/documentos/alertas` | Documentos vencidos o que vencen en ≤ 15 días |
| POST | `/api/carreras` | Registra viaje; `comision_empresa = precio × 5 %` y la suma a la liquidación PENDIENTE |
| GET | `/api/liquidaciones/conductor/{id}` | Deuda acumulada de comisiones del conductor |
| PUT | `/api/liquidaciones/{id}/pagar` | Marca la liquidación como PAGADO (cierra el corte) |

Ejemplo de registro:
```json
POST /api/conductores/registro
{ "dni": "45678912", "nombres": "Luis", "apellidos": "Quispe Rojas", "telefono": "964111222",
  "vehiculo": { "placa": "ABC-123", "tipo": "TAXI", "marca": "Toyota" },
  "documentos": [ { "tipoDoc": "SOAT", "fechaVencimiento": "2026-12-31" } ] }
```
Errores en formato `ProblemDetail` (400 validación, 404 no existe, 409 duplicado).

## Notas
- El esquema se gestiona con `db/schema.sql` (`ddl-auto=none`). Las tablas tienen RLS activado para que la API pública de Supabase no exponga los datos; el backend se conecta con el rol `postgres`.
- La API no tiene autenticación todavía (pensado para hackatón): no la expongas con datos reales sin agregar Spring Security / API key.
- Porcentaje de comisión y días de alerta configurables en `application.properties`.
- Tests: `./mvnw test` (usan H2 en memoria, no tocan Supabase).
