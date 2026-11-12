package app;

import modelo.Equipo;
import modelo.LigaPro;

/**
 * Punto de entrada del programa.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Sorteo de la Liga Profesional ===");
        System.out.println("Equipos inscritos:");
        int i = 1;
        for (Equipo e : LigaPro.equiposIniciales()) {
            System.out.printf("%2d. %-25s %s%n", i++, e.getNombre(), e.getCiudad());
        }
    }
}
