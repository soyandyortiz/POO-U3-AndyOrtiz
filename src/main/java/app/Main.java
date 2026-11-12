package app;

import java.util.Scanner;

import controlador.TorneoController;
import modelo.persistencia.ArchivoTorneo;
import vista.VistaConsola;

/**
 * Punto de entrada: arma la vista, el archivo de persistencia y el controlador.
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            VistaConsola vista = new VistaConsola(teclado);
            TorneoController controlador = new TorneoController(vista, new ArchivoTorneo());
            controlador.iniciar();
        }
    }
}
