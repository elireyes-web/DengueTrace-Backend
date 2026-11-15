# Carga de distritos para DengueTrace

El archivo `cargar-distritos.sql` carga 1 891 distritos del Perú en la tabla `districts` de PostgreSQL. Ejecuta primero el backend de DengueTrace para que cree sus tablas. Luego abre una terminal en esta carpeta y usa los datos de tu base local.

```sh
psql -h 127.0.0.1 -p 5433 -U postgres -d denguetrace -X -v ON_ERROR_STOP=1 -f cargar-distritos.sql
```

Para confirmar el resultado, consulta `GET /api/v1/districts` en tu backend local o ejecuta lo siguiente.

```sql
SELECT count(*) AS distritos, count(latitude) AS con_latitud, count(longitude) AS con_longitud
FROM districts;
```

La carga agrega registros en una transacción. Conserva los distritos existentes y se puede volver a ejecutar. Si un distrito existente comparte nombre con varios del catálogo y su provincia no permite identificarlo, la carga se cancela para que puedas revisar ese registro. En el catálogo, los distritos homónimos dentro de un departamento llevan la provincia entre paréntesis. Las coordenadas corresponden a las capitales distritales y el campo de población se deja vacío en los registros nuevos.

Fuente de datos y licencia disponibles en el [catálogo de ubigeos de 2025](https://github.com/yull23/ubigeos_peru/blob/3e2ed18eb246dd9a81b129352be6a3b6b8a96ea0/databases/ubigeo_inei_2025.csv) y en `LICENCIA-datos.txt`.
