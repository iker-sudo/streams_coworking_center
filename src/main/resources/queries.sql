-- Consultas SQL para el sistema de Coworking Center
-- Equivalentes a los 10 primeros streams de Java en CoworkingApplicationTests.java

-- 1. Devuelve un listado de todas las reservas realizadas durante el año 2025,
--    cuya sala tenga un precio_hora superior a 25€.
SELECT r.*
FROM reserva r
         INNER JOIN sala s ON r.sala_id = s.sala_id
WHERE YEAR(r.fecha) = 2025
  AND s.precio_hora > 25;

-- 2. Devuelve un listado de todos los miembros que NO han realizado ninguna reserva.
SELECT m.*
FROM miembro m
WHERE NOT EXISTS (
    SELECT 1
    FROM reserva r
    WHERE r.miembro_id = m.miembro_id
);

-- Alternativa con LEFT JOIN:
SELECT m.*
FROM miembro m
         LEFT JOIN reserva r ON m.miembro_id = r.miembro_id
WHERE r.reserva_id IS NULL;

-- 3. Devuelve una lista de los id's, nombres y emails de los miembros que no tienen el teléfono registrado.
--    El listado tiene que estar ordenado inverso alfabéticamente por nombre (z..a).
SELECT m.miembro_id AS ID, m.nombre AS Nombre, m.email AS Email
FROM miembro m
WHERE m.telefono IS NULL OR m.telefono = ''
ORDER BY m.nombre DESC;

-- 4. Devuelve un listado con los id's y emails de los miembros que se hayan registrado
--    con una cuenta de yahoo.es en el año 2024.
SELECT m.miembro_id AS ID, m.email AS Email
FROM miembro m
WHERE m.email LIKE '%@yahoo.es'
  AND YEAR(m.fecha_alta) = 2024;

-- 5. Devuelve un listado de los miembros cuyo primer apellido es Martín.
--    El listado tiene que estar ordenado por fecha de alta en el coworking
--    de más reciente a menos reciente y nombre y apellidos en orden alfabético.
SELECT m.*
FROM miembro m
WHERE m.nombre LIKE '% Martín%'
ORDER BY m.fecha_alta DESC, m.nombre ASC;

-- 6. Devuelve el gasto total (estimado) que ha realizado la miembro Ana Beltrán
--    en reservas del coworking.
SELECT SUM(
               CASE
                   WHEN r.descuento_pct IS NOT NULL THEN
                       (s.precio_hora * r.horas) - ((s.precio_hora * r.horas) * r.descuento_pct / 100)
                   ELSE
                       s.precio_hora * r.horas
                   END
           ) AS gasto_total
FROM miembro m
         INNER JOIN reserva r ON m.miembro_id = r.miembro_id
         INNER JOIN sala s ON r.sala_id = s.sala_id
WHERE m.nombre LIKE '%Ana%'
  AND m.nombre LIKE '%Beltrán%';

-- 7. Devuelve el listado de las 3 salas de menor precio_hora.
SELECT s.*
FROM sala s
ORDER BY s.precio_hora ASC
LIMIT 3;

-- 8. Devuelve la reserva a la que se le ha aplicado la mayor cuantía de descuento
--    sobre el precio sin descuento (precio_hora × horas).
SELECT r.*,
       (s.precio_hora * r.horas * r.descuento_pct / 100) AS cuantia_descuento
FROM reserva r
         INNER JOIN sala s ON r.sala_id = s.sala_id
WHERE r.descuento_pct IS NOT NULL
ORDER BY (s.precio_hora * r.horas * r.descuento_pct / 100) DESC
LIMIT 1;

-- 9. Devuelve los miembros que hayan tenido alguna reserva con estado 'ASISTIDA'
--    y exactamente 10 asistentes.
SELECT DISTINCT m.*
FROM miembro m
         INNER JOIN reserva r ON m.miembro_id = r.miembro_id
WHERE r.estado = 'ASISTIDA'
  AND r.asistentes = 10;

-- 10. Devuelve el valor mínimo de horas reservadas (campo calculado 'horas') en una reserva.
SELECT MIN(r.horas) AS horas_minimas
FROM reserva r
WHERE r.horas IS NOT NULL;

