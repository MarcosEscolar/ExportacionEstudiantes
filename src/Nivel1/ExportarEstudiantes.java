package Nivel1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExportarEstudiantes {
    static class Estudiante{
        int id;
        String nombre;
        String apellidos;
        int edad;
        double notas;

        public Estudiante(int id, String nombre,String apellidos, int edad,double notas){
            this.id = id;
            this.nombre = nombre;
            this.apellidos = apellidos;
            this.edad = edad;
            this.notas = notas;
        }
        public int getId() {return id;}

        public String getNombre() {return nombre;}

        public String getApellidos() {return apellidos;}

        public int getEdad() {return edad;}

        public double getNotas() {return notas;}
    }

    private static final String SEPARADOR = ";";
    private static final String INDENTACION = "  ";
    private static final String INDENT = "  ";

    public static void main(String[] args) {
        List<Estudiante> estudiantes = Arrays.asList(
                new Estudiante(1, "Juan", "García López", 20, 8.5),
                new Estudiante(2, "María", "Rodríguez", 19, 9.2),
                new Estudiante(3, "Pedro", "Martínez", 21, 7.8),
                new Estudiante(4, "Ana", "López", 20, 8.9),
                new Estudiante(5, "Carlos", "Sánchez", 22, 6.5)
        );

        try{
            exportarCSV(estudiantes);
            exportarXML(estudiantes);
            exportarJSON(estudiantes);
            System.out.println("Archivos creados");
        } catch (IOException e) {
            System.out.println("Error al cargar estudiantes");
        }
    }

    //ExportarCSV
    public static boolean exportarCSV(List<Estudiante> estudiantes) throws IOException {
        //Validar
        if (estudiantes == null || estudiantes.isEmpty()) {
            System.out.println("ERROR: No hay estudiantes para exportar.");
            return false;
        }


        //Crear Directorio
        File datos = new File("src/Nivel1/datos");
        if (!datos.exists()) {
            datos.mkdirs();
            System.out.println("Directorio 'src/Nivel1/datos' creado");
        } else {
            System.out.println("Ya existe el directorio 'src/Nivel1/datos'.");
        }

        //exportar
        BufferedWriter writer= null;
        String estudiantesCSV= "src/Nivel1/datos/estudiantes.csv";

        try {
            writer = new BufferedWriter(new FileWriter("src/Nivel1/datos/estudiantes.csv"));
            System.out.println("Exportando estudiantes a CSV:" + estudiantesCSV);

            //Escribir encabezado
            escribirEncabezadoCSV(writer);

            //ESCRIBIR CADA PRODUCTO
            for(Estudiante est: estudiantes){
                escribirEstudianteCSV(writer, est);
            }


            //ESCRIBIR RESUMEN
            escribirResumenCSV(writer, estudiantes);

            System.out.println("✅ Exportación completada exitosamente.");
            System.out.println("   Total de productos exportados: " + estudiantes.size());
        }catch(IOException e){
            System.out.println("Error al escribir el archivo CSV");
            return false;
        }finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("⚠️  Advertencia: Error al cerrar el archivo.");
            }
        }

        return true;
    }

    //Escribe el encabezado del CSV
    private static void escribirEncabezadoCSV(BufferedWriter writer) throws IOException {
        writer.write("# CATÁLOGO DE ESTUDIANTES - FORMATO CSV");
        writer.newLine();
        writer.write("# Generado el: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        writer.newLine();
        writer.newLine(); // Línea en blanco para separar

        // Encabezado con nombres de columnas
        writer.write("ID" + SEPARADOR);
        writer.write("Nombre" + SEPARADOR);
        writer.write("Apellidos" + SEPARADOR);
        writer.write("Edad" + SEPARADOR);
        writer.write("Notas");
        writer.newLine();
    }

    //Escribe un producto como una línea del CSV
    private static void escribirEstudianteCSV(BufferedWriter writer, Estudiante est) throws IOException {
        writer.write(est.getId() + SEPARADOR);
        writer.write(escaparCSV(est.getNombre()) + SEPARADOR);
        writer.write(escaparCSV(est.getApellidos()) + SEPARADOR);
        writer.write(est.getEdad() + SEPARADOR);
        writer.write(String.format("%.2f", est.getNotas()));
        writer.newLine();
    }

    private static String escaparCSV(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        // Si contiene el separador, comillas o saltos de línea, debemos escapar
        if (texto.contains(SEPARADOR) || texto.contains("\"") || texto.contains("\n")) {
            // Duplicamos las comillas y encerramos todo entre comillas
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }

        return texto;
    }

    private static void escribirResumenCSV(BufferedWriter writer, List<Estudiante> estudiantes) throws IOException {
        // Método vacío para mantener funcionalidad
    }

    public static boolean exportarXML(List<Estudiante> estudiantes) throws IOException {
        //Validar
        if (estudiantes == null || estudiantes.isEmpty()) {
            System.out.println("ERROR: No hay estudiantes para exportar.");
            return false;
        }

        //Crear Directorio
        File datos = new File("src/Nivel1/datos");
        if (!datos.exists()) {
            datos.mkdirs();
            System.out.println("Directorio 'src/Nivel1/datos' creado");
        } else {
            System.out.println("Ya existe el directorio 'src/Nivel1/datos'.");
        }

        //exportar
        BufferedWriter writer = null;
        String estudiantesXML = "src/Nivel1/datos/estudiantes.xml";
        try {
            writer = new BufferedWriter(new FileWriter("src/Nivel1/datos/estudiantes.xml"));

            //DECLARACIÓN XML
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();

            // COMENTARIO (opcional pero buena práctica)
            writer.write("<!-- Datos de estudiantes -->");
            writer.newLine();
            writer.write("<!-- Fecha: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                    " -->");
            writer.newLine();
            writer.newLine();

            // ELEMENTO RAÍZ (todo el contenido va dentro)
            writer.write("<clase>");
            writer.newLine();

            // METADATA (información sobre la clase)
            escribirMetadataXML(writer, estudiantes);

            // LISTA DE PRODUCTOS
            escribirEstudiantesXML(writer, estudiantes);

            // RESUMEN CON ESTADÍSTICAS
            escribirResumenXML(writer, estudiantes);

            // CERRAR ELEMENTO RAÍZ
            writer.write("</clase>");
            writer.newLine();

            System.out.println("✅ Exportación XML completada exitosamente.");
            System.out.println("   Total de productos exportados: " + estudiantes.size());
            return true;

        } catch (IOException e) {
            System.out.println("❌ ERROR al escribir el archivo XML.");
            System.out.println("   Detalles: " + e.getMessage());
            return false;

        } finally {
            // SIEMPRE cerrar el archivo
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("⚠️  Advertencia: Error al cerrar el archivo.");
            }
        }
    }

    private static void escribirMetadataXML(BufferedWriter writer, List<Estudiante> estudiantes) throws IOException {
        writer.write(INDENTACION + "<metadata>");
        writer.newLine();

        writer.write(INDENTACION + INDENTACION + "<version>1.0</version>");
        writer.newLine();

        writer.write(INDENTACION + INDENTACION + "<fechaGeneracion>");
        writer.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        writer.write("</fechaGeneracion>");
        writer.newLine();

        writer.write(INDENTACION + INDENTACION + "<totalEstudiantes>");
        writer.write(String.valueOf(estudiantes.size()));
        writer.write("</totalEstudiantes>");
        writer.newLine();

        writer.write(INDENTACION + "</metadata>");
        writer.newLine();
        writer.newLine();
    }

    // Escribe todos los estudiantes dentro del XML
    private static void escribirEstudiantesXML(BufferedWriter writer, List<Estudiante> estudiantes) throws IOException {
        writer.write("  <estudiantes>");
        writer.newLine();

        for (Estudiante e : estudiantes) {
            writer.write("    <estudiante id=\"" + e.getId() + "\">");
            writer.newLine();
            writer.write("      <nombre>" + escaparXML(e.getNombre()) + "</nombre>");
            writer.newLine();
            writer.write("      <apellidos>" + escaparXML(e.getApellidos()) + "</apellidos>");
            writer.newLine();
            writer.write("      <edad>" + e.getEdad() + "</edad>");
            writer.newLine();
            writer.write("      <nota>" + e.getNotas() + "</nota>");
            writer.newLine();
            writer.write("    </estudiante>");
            writer.newLine();
        }

        writer.write("  </estudiantes>");
        writer.newLine();
        writer.newLine();
    }

    // Escapa caracteres especiales del XML (&, <, >, ", ')
    private static String escaparXML(String texto) {
        if (texto == null) return "";
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static void escribirResumenXML(BufferedWriter writer, List<Estudiante> estudiantes) throws IOException {

    }

    public static boolean exportarJSON(List<Estudiante> estudiantes) throws IOException {
        //Validar
        if (estudiantes == null || estudiantes.isEmpty()) {
            System.out.println("ERROR: No hay estudiantes para exportar.");
            return false;
        }

        //Crear Directorio
        File datos = new File("src/Nivel1/datos");
        if (!datos.exists()) {
            datos.mkdirs();
            System.out.println("Directorio 'src/Nivel1/datos' creado");
        } else {
            System.out.println("Ya existe el directorio 'src/Nivel1/datos'.");
        }


        //Exportar
        String estudiantesJSON = "src/Nivel1/datos/estudiantes.json";
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter("src/Nivel1/datos/estudiantes.json"));

            System.out.println("\n✅ Exportando productos a JSON...");
            System.out.println("   Archivo: " + estudiantesJSON);


            writer.write("{");
            writer.newLine();

            // OBJETO CATÁLOGO
            writer.write(INDENT + "\"catalogo\": {");
            writer.newLine();

            // METADATA
            escribirMetadataJSON(writer, estudiantes);
            writer.write(","); // Coma porque vienen más elementos
            writer.newLine();

            // ARRAY DE PRODUCTOS
            escribirEstudiantesJSON(writer, estudiantes);
            writer.write(","); // Coma porque viene el resumen
            writer.newLine();

            // RESUMEN
            escribirResumenJSON(writer, estudiantes);
            // NO ponemos coma aquí porque es el último elemento
            writer.newLine();

            // CERRAR OBJETO CATÁLOGO
            writer.write(INDENT + "}");
            writer.newLine();

            // CERRAR OBJETO RAÍZ
            writer.write("}");
            writer.newLine();

            System.out.println("   Total de productos exportados: " + estudiantes.size());
            return true;

        } catch (IOException e) {
            System.out.println("❌ ERROR al escribir el archivo JSON.");
            System.out.println("   Detalles: " + e.getMessage());
            return false;

        } finally {
            // SIEMPRE cerrar el archivo
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("⚠️  Advertencia: Error al cerrar el archivo.");
            }
        }
    }

    private static void escribirMetadataJSON(BufferedWriter writer, List<Estudiante> estudiantes) throws IOException {
        String indent2 = INDENT + INDENT;

        writer.write(indent2 + "\"metadata\": {");
        writer.newLine();

        writer.write(indent2 + INDENT + "\"version\": \"1.0\",");
        writer.newLine();

        writer.write(indent2 + INDENT + "\"fecha\": \"" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\",");
        writer.newLine();

        writer.write(indent2 + INDENT + "\"totalEstudiantes\": " + estudiantes.size());
        writer.newLine();

        writer.write(indent2 + "}");
    }

    private static void escribirEstudiantesJSON(BufferedWriter writer, List<Estudiante> estudiantes)
            throws IOException {

        String indent2 = INDENT + INDENT;
        String indent3 = indent2 + INDENT;

        // Abrimos el array de productos
        writer.write(indent2 + "\"estudiantes\": [");
        writer.newLine();

        // Escribimos cada producto
        int totalEstudiantes = estudiantes.size();
        for (int i = 0; i < totalEstudiantes; i++) {
            Estudiante est = estudiantes.get(i);

            // Abrimos el objeto producto
            writer.write(indent3 + "{");
            writer.newLine();

            escribirEstudianteJSON(writer, est);

            // Cerramos el objeto producto
            writer.write(indent3 + "}");

            // Añadimos coma si NO es el último elemento del array
            if (i < totalEstudiantes - 1) {
                writer.write(",");
            }
            writer.newLine();
        }

        // Cerramos el array
        writer.write(indent2 + "]");
    }

    private static void escribirEstudianteJSON(BufferedWriter writer, Estudiante est) throws IOException {
        String indent4 = INDENT + INDENT + INDENT + INDENT;

        writer.write(indent4 + "\"id\": " + est.getId() + ",");
        writer.newLine();

        writer.write(indent4 + "\"nombre\": \"" + escaparJSON(est.getNombre()) + "\",");
        writer.newLine();

        writer.write(indent4 + "\"apellidos\": \"" + escaparJSON(est.getApellidos()) + "\",");
        writer.newLine();

        writer.write(indent4 + "\"edad\": " + est.getEdad() + ",");
        writer.newLine();

        writer.write(indent4 + "\"notas\": " + String.format(Locale.US,"%.2f", est.getNotas()));
        writer.newLine();
    }

    private static String escaparJSON(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        // IMPORTANTE: El orden importa - escapar \ primero
        return texto.replace("\\", "\\\\")   // Barra invertida primero
                .replace("\"", "\\\"")    // Comillas dobles
                .replace("\n", "\\n")     // Nueva línea
                .replace("\r", "\\r")     // Retorno de carro
                .replace("\t", "\\t");    // Tabulador
    }

    private static void escribirResumenJSON(BufferedWriter writer, List<Estudiante> estudiantes) throws IOException {
        String indent2 = INDENT + INDENT;
        writer.write(indent2 + "\"resumen\": {}");
    }
}