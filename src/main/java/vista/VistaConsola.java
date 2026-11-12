package vista;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import modelo.Equipo;
import modelo.Etapa;
import modelo.Partido;

/**
 * Vista por consola. Solo muestra información y lee lo que escribe el usuario;
 * no toma decisiones del sorteo (eso lo hacen el controlador y el modelo).
 */
public class VistaConsola {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner teclado;

    public VistaConsola(Scanner teclado) {
        this.teclado = teclado;
    }

    public int mostrarMenu() {
        System.out.println();
        System.out.println("============ SORTEO LIGA PROFESIONAL ============");
        System.out.println(" 1. Ver equipos inscritos");
        System.out.println(" 2. Inscribir un equipo");
        System.out.println(" 3. Eliminar un equipo");
        System.out.println(" 4. Realizar sorteo (octavos hasta la final)");
        System.out.println(" 5. Ver resultados del último sorteo");
        System.out.println(" 6. Guardar torneo (torneo.ser)");
        System.out.println(" 7. Cargar torneo (torneo.ser)");
        System.out.println(" 8. Restablecer los 16 equipos de la liga");
        System.out.println(" 0. Salir");
        System.out.println("=================================================");
        return leerEntero("Elija una opción: ");
    }

    public void mostrarEquipos(List<Equipo> equipos) {
        System.out.println("--- Equipos inscritos (" + equipos.size() + ") ---");
        if (equipos.isEmpty()) {
            System.out.println("  (no hay equipos)");
        }
        for (int i = 0; i < equipos.size(); i++) {
            Equipo e = equipos.get(i);
            System.out.printf("  %2d. %-25s %s%n", i + 1, e.getNombre(), e.getCiudad());
        }
    }

    public void mostrarSorteo(String nombreTorneo, Map<Etapa, List<Partido>> etapas, Equipo campeon,
                              java.time.LocalDateTime fecha) {
        System.out.println("\n##### " + nombreTorneo + " #####");
        if (fecha != null) {
            System.out.println("Sorteo realizado el " + fecha.format(FORMATO_FECHA));
        }
        for (Map.Entry<Etapa, List<Partido>> etapa : etapas.entrySet()) {
            System.out.println("\n--- " + etapa.getKey().getNombre().toUpperCase() + " ---");
            int n = 1;
            for (Partido p : etapa.getValue()) {
                System.out.println("  Partido " + n++ + ": " + p);
            }
        }
        System.out.println("\n*** CAMPEÓN: " + campeon + " ***");
    }

    public String pedirTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = teclado.nextLine().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println("  No puede quedar vacío.");
        }
    }

    public boolean confirmar(String pregunta) {
        String r = pedirTexto(pregunta + " (s/n): ");
        return r.equalsIgnoreCase("s") || r.equalsIgnoreCase("si") || r.equalsIgnoreCase("sí");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(">> " + mensaje);
    }

    public void mostrarError(String mensaje) {
        System.out.println("[ERROR] " + mensaje);
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = teclado.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("  Escriba solo el número de la opción.");
            }
        }
    }
}
