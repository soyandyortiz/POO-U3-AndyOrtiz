package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import modelo.Etapa;
import modelo.Partido;
import modelo.Torneo;
import modelo.excepciones.NumeroEquiposInvalidoException;
import modelo.excepciones.SorteoInvalidoException;
import modelo.persistencia.ArchivoTorneo;

/**
 * Punto de entrada del programa (prueba del sorteo y de la serialización).
 */
public class Main {

    public static void main(String[] args) {
        ArchivoTorneo archivo = new ArchivoTorneo();
        Torneo torneo = Torneo.conEquiposDeLaLiga();

        try {
            torneo.realizarSorteo();
            archivo.guardar(torneo);
            System.out.println("Sorteo realizado y guardado en " + archivo.getRuta());
            System.out.println("Campeón antes de guardar: " + torneo.getCampeon());

            Torneo cargado = archivo.cargar();
            System.out.println("\nTorneo leído desde " + archivo.getRuta() + ":");
            for (Map.Entry<Etapa, List<Partido>> etapa : cargado.getEtapas().entrySet()) {
                System.out.println("--- " + etapa.getKey().getNombre() + " ---");
                for (Partido p : etapa.getValue()) {
                    System.out.println("  " + p);
                }
            }
            System.out.println("Campeón después de cargar: " + cargado.getCampeon());
        } catch (NumeroEquiposInvalidoException e) {
            System.out.println("Error en el número de equipos: " + e.getMessage());
        } catch (SorteoInvalidoException e) {
            System.out.println("Error en el sorteo: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de lectura/escritura: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("El archivo tiene una clase desconocida: " + e.getMessage());
        }
    }
}
