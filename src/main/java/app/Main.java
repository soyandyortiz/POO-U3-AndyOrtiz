package app;

import java.util.List;
import java.util.Map;

import modelo.Etapa;
import modelo.Partido;
import modelo.Torneo;
import modelo.excepciones.NumeroEquiposInvalidoException;
import modelo.excepciones.SorteoInvalidoException;

/**
 * Punto de entrada del programa (prueba del sorteo recursivo).
 */
public class Main {

    public static void main(String[] args) {
        Torneo torneo = Torneo.conEquiposDeLaLiga();
        System.out.println("=== " + torneo.getNombre() + " ===");
        try {
            torneo.realizarSorteo();
            for (Map.Entry<Etapa, List<Partido>> etapa : torneo.getEtapas().entrySet()) {
                System.out.println("\n--- " + etapa.getKey().getNombre() + " ---");
                for (Partido p : etapa.getValue()) {
                    System.out.println("  " + p);
                }
            }
            System.out.println("\nCAMPEÓN: " + torneo.getCampeon());
        } catch (NumeroEquiposInvalidoException e) {
            System.out.println("Error en el número de equipos: " + e.getMessage());
        } catch (SorteoInvalidoException e) {
            System.out.println("Error en el sorteo: " + e.getMessage());
        }
    }
}
