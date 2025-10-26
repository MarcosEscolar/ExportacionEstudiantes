package Nivel2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExportarLibros {
    static class Libro {
        String isbn;
        String titulo;
        String autor;
        String categoria;
        int añoPublicacion;
        int numPaginas;
        boolean disponible;
        int prestamos;

        public Libro(String isbn, String titulo, String autor, String categoria,
                     int añoPublicacion, int numPaginas, boolean disponible, int prestamos) {
            this.isbn = isbn;
            this.titulo = titulo;
            this.autor = autor;
            this.categoria = categoria;
            this.añoPublicacion = añoPublicacion;
            this.numPaginas = numPaginas;
            this.disponible = disponible;
            this.prestamos = prestamos;
        }

        public String getIsbn() { return isbn; }
        public String getTitulo() { return titulo; }
        public String getAutor() { return autor; }
        public String getCategoria() { return categoria; }
        public int getAñoPublicacion() { return añoPublicacion; }
        public int getNumPaginas() { return numPaginas; }
        public boolean isDisponible() { return disponible; }
        public int getPrestamos() { return prestamos; }
    }

    private static final String SEPARADOR = ";";
    private static final String INDENTACION = "  ";
    private static final String INDENT = "  ";

    public static void main(String[] args) {
        List<Libro> libros = Arrays.asList(
                new Libro("978-84-123", "El Quijote", "Miguel de Cervantes", "Ficción", 1605, 863, true, 150),
                new Libro("978-84-456", "Cien años de soledad", "Gabriel García Márquez", "Ficción", 1967, 471, false, 98),
                new Libro("978-84-789", "1984", "George Orwell", "Ficción", 1949, 328, true, 120),
                new Libro("978-85-111", "Breve historia del tiempo", "Stephen Hawking", "Ciencia", 1988, 256, true, 75),
                new Libro("978-85-222", "El origen de las especies", "Charles Darwin", "Ciencia", 1859, 502, true, 45),
                new Libro("978-86-333", "Sapiens", "Yuval Noah Harari", "Historia", 2011, 498, false, 200),
                new Libro("978-86-444", "El arte de la guerra", "Sun Tzu", "Historia", 500, 273, true, 89)
        );

        // Agrupar libros por categoría
        HashMap<String, ArrayList<Libro>> librosPorCategoria = agruparPorCategoria(libros);

        try {
            exportarCSV(librosPorCategoria);
            exportarXML(librosPorCategoria);
            exportarJSON(librosPorCategoria);
            System.out.println("Archivos creados");
        } catch (IOException e) {
            System.out.println("Error al exportar libros: " + e.getMessage());
        }
    }

    // Agrupa los libros por categoría
    private static HashMap<String, ArrayList<Libro>> agruparPorCategoria(List<Libro> libros) {
        HashMap<String, ArrayList<Libro>> map = new HashMap<>();
        for (Libro libro : libros) {
            String categoria = libro.getCategoria();
            if (!map.containsKey(categoria)) {
                map.put(categoria, new ArrayList<>());
            }
            map.get(categoria).add(libro);
        }
        return map;
    }

    // EXPORTAR CSV
    public static boolean exportarCSV(HashMap<String, ArrayList<Libro>> librosPorCategoria) throws IOException {
        if (librosPorCategoria == null || librosPorCategoria.isEmpty()) {
            System.out.println("ERROR: No hay libros para exportar.");
            return false;
        }

        File datos = new File("src/Nivel2/datos");
        if (!datos.exists()) {
            datos.mkdirs();
            System.out.println("Directorio 'src/Nivel2/datos' creado");
        }

        BufferedWriter writer = null;
        String librosCSV = "src/Nivel2/datos/libros.csv";

        try {
            writer = new BufferedWriter(new FileWriter(librosCSV));
            System.out.println("Exportando libros a CSV: " + librosCSV);

            // Encabezado
            writer.write("# BIBLIOTECA MUNICIPAL - CATÁLOGO DE LIBROS");
            writer.newLine();
            writer.write("# Generado el: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.newLine();
            writer.newLine();

            // Escribir cada categoría
            for (Map.Entry<String, ArrayList<Libro>> entrada : librosPorCategoria.entrySet()) {
                String categoria = entrada.getKey();
                ArrayList<Libro> libros = entrada.getValue();

                writer.write("# CATEGORÍA: " + categoria);
                writer.newLine();
                writer.write("ISBN" + SEPARADOR + "Título" + SEPARADOR + "Autor" + SEPARADOR +
                        "Año" + SEPARADOR + "Páginas" + SEPARADOR + "Disponible" + SEPARADOR + "Préstamos");
                writer.newLine();

                int totalPrestamos = 0;
                for (Libro libro : libros) {
                    writer.write(libro.getIsbn() + SEPARADOR);
                    writer.write(escaparCSV(libro.getTitulo()) + SEPARADOR);
                    writer.write(escaparCSV(libro.getAutor()) + SEPARADOR);
                    writer.write(libro.getAñoPublicacion() + SEPARADOR);
                    writer.write(libro.getNumPaginas() + SEPARADOR);
                    writer.write(libro.isDisponible() + SEPARADOR);
                    writer.write(String.valueOf(libro.getPrestamos()));
                    writer.newLine();
                    totalPrestamos += libro.getPrestamos();
                }

                writer.write("# Subtotal " + categoria + ": " + libros.size() +
                        " libros, " + totalPrestamos + " préstamos");
                writer.newLine();
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
    public static boolean exportarXML(HashMap<String, ArrayList<Libro>> librosPorCategoria) throws IOException {
        if (librosPorCategoria == null || librosPorCategoria.isEmpty()) {
            System.out.println("ERROR: No hay libros para exportar.");
            return false;
        }

        File datos = new File("src/Nivel2/datos");
        if (!datos.exists()) {
            datos.mkdirs();
        }

        BufferedWriter writer = null;
        String librosXML = "src/Nivel2/datos/libros.xml";

        try {
            writer = new BufferedWriter(new FileWriter(librosXML));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<biblioteca>");
            writer.newLine();

            // Información
            writer.write(INDENTACION + "<informacion>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<nombre>Biblioteca Municipal</nombre>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<fecha>" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</fecha>");
            writer.newLine();

            int totalLibros = 0;
            for (ArrayList<Libro> libros : librosPorCategoria.values()) {
                totalLibros += libros.size();
            }
            writer.write(INDENTACION + INDENTACION + "<totalLibros>" + totalLibros + "</totalLibros>");
            writer.newLine();
            writer.write(INDENTACION + "</informacion>");
            writer.newLine();
            writer.newLine();

            // Categorías
            writer.write(INDENTACION + "<categorias>");
            writer.newLine();

            for (Map.Entry<String, ArrayList<Libro>> entrada : librosPorCategoria.entrySet()) {
                String categoria = entrada.getKey();
                ArrayList<Libro> libros = entrada.getValue();

                writer.write(INDENTACION + INDENTACION + "<categoria nombre=\"" +
                        escaparXML(categoria) + "\" totalLibros=\"" + libros.size() + "\">");
                writer.newLine();

                int totalPrestamos = 0;
                for (Libro libro : libros) {
                    writer.write(INDENTACION + INDENTACION + INDENTACION +
                            "<libro isbn=\"" + escaparXML(libro.getIsbn()) +
                            "\" disponible=\"" + libro.isDisponible() + "\">");
                    writer.newLine();
                    writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                            "<titulo>" + escaparXML(libro.getTitulo()) + "</titulo>");
                    writer.newLine();
                    writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                            "<autor>" + escaparXML(libro.getAutor()) + "</autor>");
                    writer.newLine();
                    writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                            "<año>" + libro.getAñoPublicacion() + "</año>");
                    writer.newLine();
                    writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                            "<paginas>" + libro.getNumPaginas() + "</paginas>");
                    writer.newLine();
                    writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                            "<prestamos>" + libro.getPrestamos() + "</prestamos>");
                    writer.newLine();
                    writer.write(INDENTACION + INDENTACION + INDENTACION + "</libro>");
                    writer.newLine();
                    totalPrestamos += libro.getPrestamos();
                }

                // Estadísticas por categoría
                double prestamosMedio = libros.size() > 0 ? (double) totalPrestamos / libros.size() : 0;
                writer.write(INDENTACION + INDENTACION + INDENTACION + "<estadisticas>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<totalPrestamos>" + totalPrestamos + "</totalPrestamos>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION +
                        "<prestamosMedio>" + String.format("%.1f", prestamosMedio) + "</prestamosMedio>");
                writer.newLine();
                writer.write(INDENTACION + INDENTACION + INDENTACION + "</estadisticas>");
                writer.newLine();

                writer.write(INDENTACION + INDENTACION + "</categoria>");
                writer.newLine();
                writer.newLine();
            }

            writer.write(INDENTACION + "</categorias>");
            writer.newLine();
            writer.newLine();

            // Resumen global
            int librosDisponibles = 0;
            int librosPrestados = 0;
            int totalPrestamosHistorico = 0;
            for (ArrayList<Libro> libros : librosPorCategoria.values()) {
                for (Libro libro : libros) {
                    if (libro.isDisponible()) {
                        librosDisponibles++;
                    } else {
                        librosPrestados++;
                    }
                    totalPrestamosHistorico += libro.getPrestamos();
                }
            }

            writer.write(INDENTACION + "<resumenGlobal>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<totalCategorias>" +
                    librosPorCategoria.size() + "</totalCategorias>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<totalLibros>" + totalLibros + "</totalLibros>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<librosDisponibles>" +
                    librosDisponibles + "</librosDisponibles>");
            writer.newLine();
            writer.write(INDENTACION + INDENTACION + "<librosPrestados>" +
                    librosPrestados + "</librosPrestados>");
            writer.newLine();
            writer.write(INDENTACION + "</resumenGlobal>");
            writer.newLine();

            writer.write("</biblioteca>");
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
    public static boolean exportarJSON(HashMap<String, ArrayList<Libro>> librosPorCategoria) throws IOException {
        if (librosPorCategoria == null || librosPorCategoria.isEmpty()) {
            System.out.println("ERROR: No hay libros para exportar.");
            return false;
        }

        File datos = new File("src/Nivel2/datos");
        if (!datos.exists()) {
            datos.mkdirs();
        }

        String librosJSON = "src/Nivel2/datos/libros.json";
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(librosJSON));

            writer.write("{");
            writer.newLine();
            writer.write(INDENT + "\"biblioteca\": {");
            writer.newLine();

            // Información
            writer.write(INDENT + INDENT + "\"informacion\": {");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"nombre\": \"Biblioteca Municipal\",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"fecha\": \"" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\",");
            writer.newLine();

            int totalLibros = 0;
            for (ArrayList<Libro> libros : librosPorCategoria.values()) {
                totalLibros += libros.size();
            }
            writer.write(INDENT + INDENT + INDENT + "\"totalLibros\": " + totalLibros);
            writer.newLine();
            writer.write(INDENT + INDENT + "},");
            writer.newLine();

            // Categorías
            writer.write(INDENT + INDENT + "\"categorias\": {");
            writer.newLine();

            int categoriaIndex = 0;
            int totalCategorias = librosPorCategoria.size();

            for (Map.Entry<String, ArrayList<Libro>> entrada : librosPorCategoria.entrySet()) {
                String categoria = entrada.getKey();
                ArrayList<Libro> libros = entrada.getValue();
                categoriaIndex++;

                writer.write(INDENT + INDENT + INDENT + "\"" + escaparJSON(categoria) + "\": {");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"totalLibros\": " + libros.size() + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"libros\": [");
                writer.newLine();

                int totalPrestamos = 0;
                Libro libroMasPrestado = null;
                int maxPrestamos = -1;

                for (int i = 0; i < libros.size(); i++) {
                    Libro libro = libros.get(i);
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + "{");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"isbn\": \"" + escaparJSON(libro.getIsbn()) + "\",");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"titulo\": \"" + escaparJSON(libro.getTitulo()) + "\",");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"autor\": \"" + escaparJSON(libro.getAutor()) + "\",");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"año\": " + libro.getAñoPublicacion() + ",");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"paginas\": " + libro.getNumPaginas() + ",");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"disponible\": " + libro.isDisponible() + ",");
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + INDENT +
                            "\"prestamos\": " + libro.getPrestamos());
                    writer.newLine();
                    writer.write(INDENT + INDENT + INDENT + INDENT + INDENT + "}");

                    if (i < libros.size() - 1) {
                        writer.write(",");
                    }
                    writer.newLine();

                    totalPrestamos += libro.getPrestamos();
                    if (libro.getPrestamos() > maxPrestamos) {
                        maxPrestamos = libro.getPrestamos();
                        libroMasPrestado = libro;
                    }
                }

                writer.write(INDENT + INDENT + INDENT + INDENT + "],");
                writer.newLine();

                // Estadísticas
                double prestamosMedio = libros.size() > 0 ? (double) totalPrestamos / libros.size() : 0;
                writer.write(INDENT + INDENT + INDENT + INDENT + "\"estadisticas\": {");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + INDENT +
                        "\"totalPrestamos\": " + totalPrestamos + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + INDENT +
                        "\"prestamosMedio\": " + String.format("%.1f", prestamosMedio) + ",");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + INDENT +
                        "\"libroMasPrestado\": \"" +
                        (libroMasPrestado != null ? escaparJSON(libroMasPrestado.getTitulo()) : "") + "\"");
                writer.newLine();
                writer.write(INDENT + INDENT + INDENT + INDENT + "}");
                writer.newLine();

                writer.write(INDENT + INDENT + INDENT + "}");
                if (categoriaIndex < totalCategorias) {
                    writer.write(",");
                }
                writer.newLine();
            }

            writer.write(INDENT + INDENT + "},");
            writer.newLine();

            // Resumen global
            int librosDisponibles = 0;
            int librosPrestados = 0;
            int totalPrestamosHistorico = 0;
            for (ArrayList<Libro> libros : librosPorCategoria.values()) {
                for (Libro libro : libros) {
                    if (libro.isDisponible()) {
                        librosDisponibles++;
                    } else {
                        librosPrestados++;
                    }
                    totalPrestamosHistorico += libro.getPrestamos();
                }
            }

            writer.write(INDENT + INDENT + "\"resumenGlobal\": {");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"totalCategorias\": " +
                    librosPorCategoria.size() + ",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"totalLibros\": " + totalLibros + ",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"librosDisponibles\": " + librosDisponibles + ",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"librosPrestados\": " + librosPrestados + ",");
            writer.newLine();
            writer.write(INDENT + INDENT + INDENT + "\"totalPrestamosHistorico\": " + totalPrestamosHistorico);
            writer.newLine();
            writer.write(INDENT + INDENT + "}");
            writer.newLine();

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