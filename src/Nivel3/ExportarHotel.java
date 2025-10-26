package Nivel3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ExportarHotel {
    static class Cliente {
        int id;
        String nombre;
        String email;
        String telefono;

        public Cliente(int id, String nombre, String email, String telefono) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.telefono = telefono;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getEmail() { return email; }
        public String getTelefono() { return telefono; }
    }

    static class Habitacion {
        int numero;
        String tipo;
        double precioPorNoche;
        boolean disponible;

        public Habitacion(int numero, String tipo, double precioPorNoche, boolean disponible) {
            this.numero = numero;
            this.tipo = tipo;
            this.precioPorNoche = precioPorNoche;
            this.disponible = disponible;
        }

        public int getNumero() { return numero; }
        public String getTipo() { return tipo; }
        public double getPrecioPorNoche() { return precioPorNoche; }
        public boolean isDisponible() { return disponible; }
    }

    static class Reserva {
        int id;
        Cliente cliente;
        Habitacion habitacion;
        LocalDate fechaEntrada;
        LocalDate fechaSalida;
        int noches;
        double precioTotal;
        String estado;

        public Reserva(int id, Cliente cliente, Habitacion habitacion,
                       LocalDate fechaEntrada, LocalDate fechaSalida, String estado) {
            this.id = id;
            this.cliente = cliente;
            this.habitacion = habitacion;
            this.fechaEntrada = fechaEntrada;
            this.fechaSalida = fechaSalida;
            this.noches = (int) ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
            this.precioTotal = this.noches * habitacion.getPrecioPorNoche();
            this.estado = estado;
        }

        public int getId() { return id; }
        public Cliente getCliente() { return cliente; }
        public Habitacion getHabitacion() { return habitacion; }
        public LocalDate getFechaEntrada() { return fechaEntrada; }
        public LocalDate getFechaSalida() { return fechaSalida; }
        public int getNoches() { return noches; }
        public double getPrecioTotal() { return precioTotal; }
        public String getEstado() { return estado; }
    }

    private static final String SEPARADOR = ";";
    private static final String INDENTACION = "  ";
    private static final String INDENT = "  ";

    public static void main(String[] args) {
        // Crear clientes
        Cliente c1 = new Cliente(1, "Juan García", "juan@email.com", "666111222");
        Cliente c2 = new Cliente(2, "María López", "maria@email.com", "666222333");
        Cliente c3 = new Cliente(3, "Pedro Martínez", "pedro@email.com", "666333444");
        Cliente c4 = new Cliente(4, "Ana Sánchez", "ana@email.com", "666444555");

        // Crear habitaciones
        Habitacion h1 = new Habitacion(101, "Doble", 90.00, false);
        Habitacion h2 = new Habitacion(102, "Individual", 50.00, true);
        Habitacion h3 = new Habitacion(103, "Individual", 50.00, false);
        Habitacion h4 = new Habitacion(201, "Suite", 200.00, false);
        Habitacion h5 = new Habitacion(202, "Doble", 90.00, true);
        Habitacion h6 = new Habitacion(203, "Individual", 50.00, true);
        Habitacion h7 = new Habitacion(204, "Doble", 90.00, false);
        Habitacion h8 = new Habitacion(205, "Suite", 200.00, false);

        // Crear reservas
        List<Reserva> reservas = Arrays.asList(
                new Reserva(1, c1, h1, LocalDate.parse("2025-10-20"), LocalDate.parse("2025-10-23"), "Confirmada"),
                new Reserva(2, c2, h8, LocalDate.parse("2025-10-21"), LocalDate.parse("2025-10-25"), "Confirmada"),
                new Reserva(3, c3, h3, LocalDate.parse("2025-10-22"), LocalDate.parse("2025-10-24"), "Confirmada"),
                new Reserva(4, c1, h7, LocalDate.parse("2025-11-01"), LocalDate.parse("2025-11-05"), "Confirmada"),
                new Reserva(5, c4, h4, LocalDate.parse("2025-11-10"), LocalDate.parse("2025-11-14"), "Confirmada"),
                new Reserva(6, c2, h3, LocalDate.parse("2025-09-15"), LocalDate.parse("2025-09-17"), "Completada"),
                new Reserva(7, c3, h2, LocalDate.parse("2025-09-20"), LocalDate.parse("2025-09-22"), "Completada"),
                new Reserva(8, c4, h7, LocalDate.parse("2025-10-15"), LocalDate.parse("2025-10-18"), "Confirmada"),
                new Reserva(9, c1, h4, LocalDate.parse("2025-10-25"), LocalDate.parse("2025-10-28"), "Confirmada"),
                new Reserva(10, c3, h1, LocalDate.parse("2025-11-20"), LocalDate.parse("2025-11-22"), "Cancelada")
        );

        // Crear mapa de clientes
        HashMap<Integer, Cliente> clientesMap = new HashMap<>();
        clientesMap.put(c1.getId(), c1);
        clientesMap.put(c2.getId(), c2);
        clientesMap.put(c3.getId(), c3);
        clientesMap.put(c4.getId(), c4);

        // Crear mapa de habitaciones
        HashMap<Integer, Habitacion> habitacionesMap = new HashMap<>();
        habitacionesMap.put(h1.getNumero(), h1);
        habitacionesMap.put(h2.getNumero(), h2);
        habitacionesMap.put(h3.getNumero(), h3);
        habitacionesMap.put(h4.getNumero(), h4);
        habitacionesMap.put(h5.getNumero(), h5);
        habitacionesMap.put(h6.getNumero(), h6);
        habitacionesMap.put(h7.getNumero(), h7);
        habitacionesMap.put(h8.getNumero(), h8);

        try {
            exportarCSV(reservas);
            exportarXML(reservas);
            exportarJSON(reservas, clientesMap, habitacionesMap);
            System.out.println("Archivos creados");
        } catch (IOException e) {
            System.out.println("Error al exportar reservas: " + e.getMessage());
        }
    }

    //  EXPORTAR CSV
    public static boolean exportarCSV(List<Reserva> reservas) throws IOException {
        if (reservas == null || reservas.isEmpty()) {
            System.out.println("ERROR: No hay reservas para exportar.");
            return false;
        }

        File datos = new File("src/Nivel3/datos");
        if (!datos.exists()) {
            datos.mkdirs();
            System.out.println("Directorio 'src/Nivel3/datos' creado");
        }

        BufferedWriter writer = null;
        String reservasCSV = "src/Nivel3/datos/reservas.csv";

        try {
            writer = new BufferedWriter(new FileWriter(reservasCSV));
            System.out.println("Exportando reservas a CSV: " + reservasCSV);

            // Encabezado
            writer.write("# HOTEL - SISTEMA DE RESERVAS");
            writer.newLine();
            writer.write("# Generado el: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.newLine();
            writer.newLine();

            // Encabezado de columnas
            writer.write("ID" + SEPARADOR);
            writer.write("ClienteNombre" + SEPARADOR);
            writer.write("ClienteEmail" + SEPARADOR);
            writer.write("HabitacionNum" + SEPARADOR);
            writer.write("TipoHabitacion" + SEPARADOR);
            writer.write("FechaEntrada" + SEPARADOR);
            writer.write("FechaSalida" + SEPARADOR);
            writer.write("Noches" + SEPARADOR);
            writer.write("PrecioTotal" + SEPARADOR);
            writer.write("Estado");
            writer.newLine();

            // Escribir cada reserva
            for (Reserva reserva : reservas) {
                writer.write(reserva.getId() + SEPARADOR);
                writer.write(escaparCSV(reserva.getCliente().getNombre()) + SEPARADOR);
                writer.write(escaparCSV(reserva.getCliente().getEmail()) + SEPARADOR);
                writer.write(reserva.getHabitacion().getNumero() + SEPARADOR);
                writer.write(reserva.getHabitacion().getTipo() + SEPARADOR);
                writer.write(reserva.getFechaEntrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + SEPARADOR);
                writer.write(reserva.getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + SEPARADOR);
                writer.write(reserva.getNoches() + SEPARADOR);
                writer.write(String.format("%.2f", reserva.getPrecioTotal()) + SEPARADOR);
                writer.write(reserva.getEstado());
                writer.newLine();
            }

            System.out.println("✅ Exportación CSV completada exitosamente.");
            return true;

        } catch (IOException e) {
            System.out.println("Error al escribir el archivo CSV");
            return false;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static String escaparCSV(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        if (texto.contains(SEPARADOR) || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    // EXPORTAR XML
    public static boolean exportarXML(List<Reserva> reservas) throws IOException {
        if (reservas == null || reservas.isEmpty()) {
            System.out.println("ERROR: No hay reservas para exportar.");
            return false;
        }

        File datos = new File("src/Nivel3/datos");
        if (!datos.exists()) {
            datos.mkdirs();
        }

        BufferedWriter writer = null;
        String reservasXML = "src/Nivel3/datos/reservas.xml";

        try {
            writer = new BufferedWriter(new FileWriter(reservasXML));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<hotel>");
            writer.newLine();

            // Información
            writer.write(INDENTACION + "<informacion>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<nombre>Hotel Paradise</nombre>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<fecha>" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</fecha>");
            writer.newLine();
            writer.write(INDENTACION + "</informacion>");
            writer.newLine();
            writer.newLine();

            // Reservas
            writer.write(INDENTACION + "<reservas totalReservas=\"" + reservas.size() + "\">");
            writer.newLine();

            for (Reserva reserva : reservas) {
                writer.write(INDENTACION + INDENTACION + "<reserva id=\"" + reserva.getId() +
                        "\" estado=\"" + reserva.getEstado() + "\">");
                writer.newLine();

                // Cliente
                writer.write(INDENTACION + INDENTACION + INDENTACION + "<cliente>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<id>" + reserva.getCliente().getId() + "</id>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<nombre>" + escaparXML(reserva.getCliente().getNombre()) + "</nombre>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<email>" + escaparXML(reserva.getCliente().getEmail()) + "</email>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<telefono>" + escaparXML(reserva.getCliente().getTelefono()) + "</telefono>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + "</cliente>");
                writer.newLine();

                // Habitación
                writer.write(INDENTACION + INDENTACION + INDENTACION + "<habitacion numero=\"" +
                        reserva.getHabitacion().getNumero() + "\" tipo=\"" +
                        reserva.getHabitacion().getTipo() + "\">");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<precioPorNoche>" + String.format("%.2f", reserva.getHabitacion().getPrecioPorNoche()) +
                        "</precioPorNoche>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<disponible>" + reserva.getHabitacion().isDisponible() + "</disponible>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + "</habitacion>");
                writer.newLine();

                // Fechas
                writer.write(INDENTACION + INDENTACION + INDENTACION + "<fechas>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<entrada>" + reserva.getFechaEntrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        "</entrada>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<salida>" + reserva.getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        "</salida>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<noches>" + reserva.getNoches() + "</noches>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + "</fechas>");
                writer.newLine();

                // Precio
                writer.write(INDENTACION + INDENTACION + INDENTACION + "<precio>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<total>" + String.format("%.2f", reserva.getPrecioTotal()) + "</total>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<porNoche>" + String.format("%.2f", reserva.getHabitacion().getPrecioPorNoche()) +
                        "</porNoche>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + "</precio>");
                writer.newLine();

                writer.write(INDENTACION + INDENTACION + "</reserva>");
                writer.newLine();
            }

            writer.write(INDENTACION + "</reservas>");
            writer.newLine();
            writer.newLine();

            // Estadísticas
            escribirEstadisticasXML(writer, reservas);

            writer.write("</hotel>");
            writer.newLine();

            System.out.println("✅ Exportación XML completada exitosamente.");
            return true;

        } catch (IOException e) {
            System.out.println("❌ ERROR al escribir el archivo XML: " + e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void escribirEstadisticasXML(BufferedWriter writer, List<Reserva> reservas) throws IOException {
        // Calcular estadísticas por tipo de habitación
        HashMap<String, Integer> reservasPorTipo = new HashMap<>();
        HashMap<String, Double> ingresosPorTipo = new HashMap<>();
        HashMap<String, Integer> reservasPorEstado = new HashMap<>();
        double ingresosTotal = 0;
        int nochesTotal = 0;

        for (Reserva reserva : reservas) {
            String tipo = reserva.getHabitacion().getTipo();
            String estado = reserva.getEstado();

            reservasPorTipo.put(tipo, reservasPorTipo.getOrDefault(tipo, 0) + 1);
            ingresosPorTipo.put(tipo, ingresosPorTipo.getOrDefault(tipo, 0.0) + reserva.getPrecioTotal());
            reservasPorEstado.put(estado, reservasPorEstado.getOrDefault(estado, 0) + 1);
            ingresosTotal += reserva.getPrecioTotal();
            nochesTotal += reserva.getNoches();
        }

        writer.write(INDENTACION + "<estadisticas>");
        writer.newLine();

        // Por tipo de habitación
        writer.write(INDENTACION + INDENTACION + "<porTipoHabitacion>");
        writer.newLine();

        for (Map.Entry<String, Integer> entrada : reservasPorTipo.entrySet()) {
            String tipo = entrada.getKey();
            writer.write(INDENTACION + INDENTACION + INDENTACION + "<" + tipo + ">");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                    "<totalReservas>" + entrada.getValue() + "</totalReservas>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                    "<ingresos>" + String.format("%.2f", ingresosPorTipo.get(tipo)) + "</ingresos>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + INDENTACION + "</" + tipo + ">");
            writer.newLine();
        }

        writer.write(INDENTACION + INDENTACION + "</porTipoHabitacion>");
        writer.newLine();

        // Por estado
        writer.write(INDENTACION + INDENTACION + "<porEstado>");
        writer.newLine();
        for (Map.Entry<String, Integer> entrada : reservasPorEstado.entrySet()) {
            writer.write(INDENTACION + INDENTACION + INDENTACION + "<" + entrada.getKey() + ">" +
                    entrada.getValue() + "</" + entrada.getKey() + ">");
            writer.newLine();
        }
        writer.write(INDENTACION + INDENTACION + "</porEstado>");
        writer.newLine();

        // Resumen
        writer.write(INDENTACION + INDENTACION + "<resumen>");
        writer.newLine();
        writer.write(INDENTACION + INDENTACION + INDENTACION +
                "<totalReservas>" + reservas.size() + "</totalReservas>");
        writer.newLine();
        writer.write(INDENTACION + INDENTACION + INDENTACION +
                "<ingresosTotal>" + String.format("%.2f", ingresosTotal) + "</ingresosTotal>");
        writer.newLine();
        writer.write(INDENTACION + INDENTACION + INDENTACION +
                "<nochesReservadas>" + nochesTotal + "</nochesReservadas>");
        writer.newLine();
        writer.write(INDENTACION + INDENTACION + "</resumen>");
        writer.newLine();

        writer.write(INDENTACION + "</estadisticas>");
        writer.newLine();
    }

    private static String escaparXML(String texto) {
        if (texto == null) return "";
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // EXPORTAR JSON
    public static boolean exportarJSON(List<Reserva> reservas, HashMap<Integer, Cliente> clientes,
                                       HashMap<Integer, Habitacion> habitaciones) throws IOException {
        if (reservas == null || reservas.isEmpty()) {
            System.out.println("ERROR: No hay reservas para exportar.");
            return false;
        }

        File datos = new File("src/Nivel3/datos");
        if (!datos.exists()) {
            datos.mkdirs();
        }

        String reservasJSON = "src/Nivel3/datos/reservas.json";
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(reservasJSON));

            writer.write("{");
            writer.newLine();
            writer.write(INDENT + "\"hotel\": {");
            writer.newLine();

            // Información
            writer.write(INDENT + INDENT + "\"informacion\": {");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"nombre\": \"Hotel Paradise\",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"fecha\": \"" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\"");
            writer.newLine();
            writer.write(INDENT + INDENT + "},");
            writer.newLine();

            // Clientes
            writer.write(INDENT + INDENT + "\"clientes\": {");
            writer.newLine();
            int clienteIndex = 0;
            for (Map.Entry<Integer, Cliente> entrada : clientes.entrySet()) {
                clienteIndex++;
                Cliente c = entrada.getValue();
                writer.write(INDENT + INDENT + INDENT + "\"" + c.getId() + "\": {");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"nombre\": \"" +
                        escaparJSON(c.getNombre()) + "\",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"email\": \"" +
                        escaparJSON(c.getEmail()) + "\",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"telefono\": \"" +
                        escaparJSON(c.getTelefono()) + "\"");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + "}");
                if (clienteIndex < clientes.size()) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write(INDENT + INDENT + "},");
            writer.newLine();

            // Habitaciones
            writer.write(INDENT + INDENT + "\"habitaciones\": {");
            writer.newLine();
            int habitacionIndex = 0;
            for (Map.Entry<Integer, Habitacion> entrada : habitaciones.entrySet()) {
                habitacionIndex++;
                Habitacion h = entrada.getValue();
                writer.write(INDENT + INDENT + INDENT + "\"" + h.getNumero() + "\": {");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"tipo\": \"" + h.getTipo() + "\",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"precioPorNoche\": " +
                        String.format(Locale.US,"%.2f", h.getPrecioPorNoche()) + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"disponible\": " + h.isDisponible());
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + "}");
                if (habitacionIndex < habitaciones.size()) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write(INDENT + INDENT + "},");
            writer.newLine();

            // Reservas
            writer.write(INDENT + INDENT + "\"reservas\": [");
            writer.newLine();
            for (int i = 0; i < reservas.size(); i++) {
                Reserva r = reservas.get(i);
                writer.write(INDENT + INDENT + INDENT + "{");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"id\": " + r.getId() + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"clienteId\": " +
                        r.getCliente().getId() + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"habitacionNumero\": " +
                        r.getHabitacion().getNumero() + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"fechaEntrada\": \"" +
                        r.getFechaEntrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"fechaSalida\": \"" +
                        r.getFechaSalida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"noches\": " + r.getNoches() + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"precioTotal\": " +
                        String.format(Locale.US,"%.2f", r.getPrecioTotal()) + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"estado\": \"" + r.getEstado() + "\"");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + "}");
                if (i < reservas.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write(INDENT + INDENT + "],");
            writer.newLine();

            // Estadísticas
            escribirEstadisticasJSON(writer, reservas);

            writer.write(INDENT + "}");
            writer.newLine();
            writer.write("}");
            writer.newLine();

            System.out.println("✅ Exportación JSON completada exitosamente.");
            return true;

        } catch (IOException e) {
            System.out.println("❌ ERROR al escribir el archivo JSON: " + e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void escribirEstadisticasJSON(BufferedWriter writer, List<Reserva> reservas) throws IOException {
        // Calcular estadísticas
        HashMap<String, Integer> reservasPorTipo = new HashMap<>();
        HashMap<String, Double> ingresosPorTipo = new HashMap<>();
        HashMap<String, Integer> reservasPorEstado = new HashMap<>();
        double ingresosTotal = 0;
        int nochesTotal = 0;

        for (Reserva reserva : reservas) {
            String tipo = reserva.getHabitacion().getTipo();
            String estado = reserva.getEstado();

            reservasPorTipo.put(tipo, reservasPorTipo.getOrDefault(tipo, 0) + 1);
            ingresosPorTipo.put(tipo, ingresosPorTipo.getOrDefault(tipo, 0.0) + reserva.getPrecioTotal());
            reservasPorEstado.put(estado, reservasPorEstado.getOrDefault(estado, 0) + 1);
            ingresosTotal += reserva.getPrecioTotal();
            nochesTotal += reserva.getNoches();
        }

        writer.write(INDENT + INDENT + "\"estadisticas\": {");
        writer.newLine();

        // Por tipo de habitación
        writer.write(INDENT + INDENT + INDENT + "\"porTipoHabitacion\": {");
        writer.newLine();

        int tipoIndex = 0;
        for (Map.Entry<String, Integer> entrada : reservasPorTipo.entrySet()) {
            tipoIndex++;
            String tipo = entrada.getKey();
            double porcentaje = (entrada.getValue() * 100.0) / reservas.size();

            writer.write(INDENT + INDENT + INDENT + INDENT + "\"" + tipo + "\": {");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + "\"totalReservas\": " +
                    entrada.getValue() + ",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + "\"ingresos\": " +
                    String.format(Locale.US,"%.2f", ingresosPorTipo.get(tipo)) + ",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + "\"porcentaje\": " +
                    String.format(Locale.US,"%.1f", porcentaje));
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + INDENT + "}");
            if (tipoIndex < reservasPorTipo.size()) {
                writer.write(",");
            }
            writer.newLine();
        }

        writer.write(INDENT + INDENT + INDENT + "},");
        writer.newLine();

        // Por estado
        writer.write(INDENT + INDENT + INDENT + "\"porEstado\": {");
        writer.newLine();
        int estadoIndex = 0;
        for (Map.Entry<String, Integer> entrada : reservasPorEstado.entrySet()) {
            estadoIndex++;
            writer.write(INDENT + INDENT + INDENT + INDENT + "\"" + entrada.getKey() + "\": " +
                    entrada.getValue());
            if (estadoIndex < reservasPorEstado.size()) {
                writer.write(",");
            }
            writer.newLine();
        }
        writer.write(INDENT + INDENT + INDENT + "},");
        writer.newLine();

        // Resumen
        double ocupacionMedia = (nochesTotal * 100.0) / (reservas.size() * 5); // Estimación simple
        writer.write(INDENT + INDENT + INDENT + "\"resumen\": {");
        writer.newLine();
        writer.write(INDENT + INDENT + INDENT + INDENT + "\"totalReservas\": " + reservas.size() + ",");
        writer.newLine();
        writer.write(INDENT + INDENT + INDENT + INDENT + "\"ingresosTotal\": " +
                String.format(Locale.US,"%.2f", ingresosTotal) + ",");
        writer.newLine();
        writer.write(INDENT + INDENT + INDENT + INDENT + "\"nochesReservadas\": " + nochesTotal + ",");
        writer.newLine();
        writer.write(INDENT + INDENT + INDENT + INDENT + "\"ocupacionMedia\": " +
                String.format(Locale.US,"%.1f", ocupacionMedia));
        writer.newLine();
        writer.write(INDENT + INDENT + INDENT + "}");
        writer.newLine();

        writer.write(INDENT + INDENT + "}");
        writer.newLine();
    }

    private static String escaparJSON(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
