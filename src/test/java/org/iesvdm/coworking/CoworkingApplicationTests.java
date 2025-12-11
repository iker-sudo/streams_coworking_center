package org.iesvdm.coworking;

import org.iesvdm.coworking.repositorio.MiembroRepository;
import org.iesvdm.coworking.repositorio.ReservaRepository;
import org.iesvdm.coworking.repositorio.SalaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoworkingApplicationTests {

    @Autowired
    MiembroRepository miembroRepository;

    @Autowired
    ReservaRepository reservaRepository;

    @Autowired
    SalaRepository salaRepository;

    @Test
    void testMiembros() {

        miembroRepository.findAll().forEach(System.out::println);

    }

    @Test
    void testReservas() {

        reservaRepository.findAll().forEach(System.out::println);

    }

    @Test
    void testSalas() {

        salaRepository.findAll().forEach(System.out::println);

    }

    //1. Devuelve un listado de todas las reservas realizadas durante el año 2025, cuya sala tenga un precio_hora superior a 25€.
    @Test
    void test1() {
        reservaRepository.findAll().stream()
                .filter(r -> r.getFecha().getYear() == 2025)
                .filter(r -> r.getSala().getPrecioHora().compareTo(new java.math.BigDecimal("25")) > 0)
                .forEach(System.out::println);
    }

    // 2. Devuelve un listado de todos los miembros que NO han realizado ninguna reserva.
    @Test
    void test2() {
        miembroRepository.findAll().stream()
                .filter(m -> m.getReservas().isEmpty())
                .forEach(System.out::println);
    }

    // 3. Devuelve una lista de los id's, nombres y emails de los miembros que no tienen el teléfono registrado.
    // El listado tiene que estar ordenado inverso alfabéticamente por nombre (z..a).
    @Test
    void test3() {
        miembroRepository.findAll().stream()
                .filter(m -> m.getTelefono() == null || m.getTelefono().isEmpty())
                .sorted((m1, m2) -> m2.getNombre().compareTo(m1.getNombre()))
                .forEach(m -> System.out.println("ID: " + m.getId() + ", Nombre: " + m.getNombre() + ", Email: " + m.getEmail()));
    }

    // 4. Devuelve un listado con los id's y emails de los miembros que se hayan registrado con una cuenta de yahoo.es
    // en el año 2024.
    @Test
    void test4() {
        miembroRepository.findAll().stream()
                .filter(m -> m.getEmail().endsWith("@yahoo.es"))
                .filter(m -> m.getFechaAlta().getYear() == 2024)
                .forEach(m -> System.out.println("ID: " + m.getId() + ", Email: " + m.getEmail()));
    }

    // 5. Devuelve un listado de los miembros cuyo primer apellido es Martín. El listado tiene que estar ordenado
    // por fecha de alta en el coworking de más reciente a menos reciente y nombre y apellidos en orden alfabético.
    @Test
    void test5() {
        miembroRepository.findAll().stream()
                .filter(m -> m.getNombre().contains(" Martín"))
                .sorted(java.util.Comparator.comparing(org.iesvdm.coworking.modelo.Miembro::getFechaAlta).reversed()
                        .thenComparing(org.iesvdm.coworking.modelo.Miembro::getNombre))
                .forEach(System.out::println);
    }

    // 6. Devuelve el gasto total (estimado) que ha realizado la miembro Ana Beltrán en reservas del coworking.
    @Test
    void test6() {
        java.math.BigDecimal total = miembroRepository.findAll().stream()
                .filter(m -> m.getNombre().contains("Ana") && m.getNombre().contains("Beltrán"))
                .flatMap(m -> m.getReservas().stream())
                .map(r -> {
                    java.math.BigDecimal precioBase = r.getSala().getPrecioHora().multiply(r.getHoras());
                    if (r.getDescuentoPct() != null) {
                        java.math.BigDecimal descuento = precioBase.multiply(r.getDescuentoPct()).divide(new java.math.BigDecimal("100"));
                        return precioBase.subtract(descuento);
                    }
                    return precioBase;
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        System.out.println("Gasto total de Ana Beltrán: " + total + "€");
    }

    // 7. Devuelve el listado de las 3 salas de menor precio_hora.
    @Test
    void test7() {
        salaRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(org.iesvdm.coworking.modelo.Sala::getPrecioHora))
                .limit(3)
                .forEach(System.out::println);
    }

    // 8. Devuelve la reserva a la que se le ha aplicado la mayor cuantía de descuento sobre el precio sin descuento
    // (precio_hora × horas).
    @Test
    void test8() {
        reservaRepository.findAll().stream()
                .filter(r -> r.getDescuentoPct() != null)
                .max(java.util.Comparator.comparing(r -> {
                    java.math.BigDecimal precioBase = r.getSala().getPrecioHora().multiply(r.getHoras());
                    return precioBase.multiply(r.getDescuentoPct()).divide(new java.math.BigDecimal("100"));
                }))
                .ifPresent(System.out::println);
    }

    // 9. Devuelve los miembros que hayan tenido alguna reserva con estado 'ASISTIDA' y exactamente 10 asistentes.
    @Test
    void test9() {
        miembroRepository.findAll().stream()
                .filter(m -> m.getReservas().stream()
                        .anyMatch(r -> "ASISTIDA".equals(r.getEstado()) && r.getAsistentes() != null && r.getAsistentes() == 10))
                .forEach(System.out::println);
    }

    // 10. Devuelve el valor mínimo de horas reservadas (campo calculado 'horas') en una reserva.
    @Test
    void test10() {
        reservaRepository.findAll().stream()
                .map(org.iesvdm.coworking.modelo.Reserva::getHoras)
                .filter(h -> h != null)
                .min(java.math.BigDecimal::compareTo)
                .ifPresent(h -> System.out.println("Horas mínimas: " + h));
    }

    // 11. Devuelve un listado de las salas que empiecen por 'Sala' y terminen por 'o',
    // y también las salas que terminen por 'x'.
    @Test
    void test11() {
        salaRepository.findAll().stream()
                .filter(s -> (s.getNombre().startsWith("Sala") && s.getNombre().endsWith("o"))
                        || s.getNombre().endsWith("x"))
                .forEach(System.out::println);
    }

    // 12. Devuelve un listado que muestre todas las reservas y salas en las que se ha registrado cada miembro.
    // El resultado debe mostrar todos los datos del miembro primero junto con un sublistado de sus reservas y salas.
    // El listado debe mostrar los datos de los miembros ordenados alfabéticamente por nombre.
    @Test
    void test12() {
        miembroRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(org.iesvdm.coworking.modelo.Miembro::getNombre))
                .forEach(m -> {
                    System.out.println(m);
                    m.getReservas().forEach(r -> System.out.println("  Reserva: " + r.getId() + ", Sala: " + r.getSala().getNombre()));
                });
    }

    // 13. Devuelve el total de personas que podrían alojarse simultáneamente en el centro en base al aforo de todas las salas.
    @Test
    void test13() {
        int totalAforo = salaRepository.findAll().stream()
                .mapToInt(s -> s.getAforo() != null ? s.getAforo() : 0)
                .sum();
        System.out.println("Aforo total: " + totalAforo);
    }

    // 14. Calcula el número total de miembros (diferentes) que tienen alguna reserva.
    @Test
    void test14() {
        long totalMiembros = reservaRepository.findAll().stream()
                .map(org.iesvdm.coworking.modelo.Reserva::getMiembro)
                .distinct()
                .count();
        System.out.println("Total de miembros con reserva: " + totalMiembros);
    }

    // 15. Devuelve el listado de las salas para las que se aplica un descuento porcentual (descuento_pct) superior al 10%
    // en alguna de sus reservas.
    @Test
    void test15() {
        salaRepository.findAll().stream()
                .filter(s -> s.getReservas().stream()
                        .anyMatch(r -> r.getDescuentoPct() != null && r.getDescuentoPct().compareTo(new java.math.BigDecimal("10")) > 0))
                .forEach(System.out::println);
    }

    // 16. Devuelve el nombre del miembro que pagó la reserva de mayor cuantía (precio_hora × horas aplicando el descuento).
    @Test
    void test16() {
        reservaRepository.findAll().stream()
                .max(java.util.Comparator.comparing(r -> {
                    java.math.BigDecimal precioBase = r.getSala().getPrecioHora().multiply(r.getHoras());
                    if (r.getDescuentoPct() != null) {
                        java.math.BigDecimal descuento = precioBase.multiply(r.getDescuentoPct()).divide(new java.math.BigDecimal("100"));
                        return precioBase.subtract(descuento);
                    }
                    return precioBase;
                }))
                .ifPresent(r -> System.out.println("Miembro con mayor cuantía: " + r.getMiembro().getNombre()));
    }

    // 17. Devuelve los nombres de los miembros que hayan coincidido en alguna reserva con la miembro Ana Beltrán
    // (misma sala y fecha con solape horario).
    @Test
    void test17() {
        var reservasAnaBeltran = miembroRepository.findAll().stream()
                .filter(m -> m.getNombre().contains("Ana") && m.getNombre().contains("Beltrán"))
                .flatMap(m -> m.getReservas().stream())
                .toList();

        miembroRepository.findAll().stream()
                .filter(m -> !(m.getNombre().contains("Ana") && m.getNombre().contains("Beltrán")))
                .filter(m -> m.getReservas().stream()
                        .anyMatch(r1 -> reservasAnaBeltran.stream()
                                .anyMatch(r2 -> r1.getSala().getId().equals(r2.getSala().getId())
                                        && r1.getFecha().equals(r2.getFecha())
                                        && r1.getHoraInicio().isBefore(r2.getHoraFin())
                                        && r1.getHoraFin().isAfter(r2.getHoraInicio()))))
                .map(org.iesvdm.coworking.modelo.Miembro::getNombre)
                .distinct()
                .forEach(System.out::println);
    }

    // 18. Devuelve el total de lo ingresado por el coworking en reservas para el mes de enero de 2025.
    @Test
    void test18() {
        java.math.BigDecimal totalEnero = reservaRepository.findAll().stream()
                .filter(r -> r.getFecha().getYear() == 2025 && r.getFecha().getMonthValue() == 1)
                .map(r -> {
                    java.math.BigDecimal precioBase = r.getSala().getPrecioHora().multiply(r.getHoras());
                    if (r.getDescuentoPct() != null) {
                        java.math.BigDecimal descuento = precioBase.multiply(r.getDescuentoPct()).divide(new java.math.BigDecimal("100"));
                        return precioBase.subtract(descuento);
                    }
                    return precioBase;
                })
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        System.out.println("Total ingresado en enero 2025: " + totalEnero + "€");
    }

    // 19. Devuelve el conteo de cuántos miembros tienen la observación 'Requiere equipamiento especial' en alguna de sus reservas.
    @Test
    void test19() {
        long totalMiembros = miembroRepository.findAll().stream()
                .filter(m -> m.getReservas().stream()
                        .anyMatch(r -> r.getObservaciones() != null && r.getObservaciones().contains("Requiere equipamiento especial")))
                .count();
        System.out.println("Miembros con 'Requiere equipamiento especial': " + totalMiembros);
    }

    // 20. Devuelve cuánto se ingresaría por la sala 'Auditorio Sol' si estuviera reservada durante todo su horario de apertura
    // en un día completo (sin descuentos).
    @Test
    void test20() {
        salaRepository.findAll().stream()
                .filter(s -> "Auditorio Sol".equals(s.getNombre()))
                .findFirst()
                .ifPresent(s -> {
                    long horasApertura = java.time.Duration.between(s.getApertura(), s.getCierre()).toHours();
                    java.math.BigDecimal ingreso = s.getPrecioHora().multiply(new java.math.BigDecimal(horasApertura));
                    System.out.println("Ingreso por día completo en Auditorio Sol: " + ingreso + "€");
                });
    }
}
