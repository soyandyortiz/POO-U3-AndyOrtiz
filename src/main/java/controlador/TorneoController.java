package controlador;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;

import modelo.Equipo;
import modelo.Torneo;
import modelo.excepciones.NumeroEquiposInvalidoException;
import modelo.excepciones.SorteoInvalidoException;
import modelo.persistencia.ArchivoTorneo;
import vista.VistaConsola;

/**
 * Controlador: recibe lo que el usuario elige en la vista, llama al modelo
 * (sorteo y persistencia) y le pasa los resultados a la vista.
 * Aquí se capturan las excepciones para que el programa no se caiga.
 */
public class TorneoController {

    private final VistaConsola vista;
    private final ArchivoTorneo archivo;
    private Torneo torneo;

    public TorneoController(VistaConsola vista, ArchivoTorneo archivo) {
        this.vista = vista;
        this.archivo = archivo;
    }

    public void iniciar() {
        cargarAlIniciar();
        int opcion;
        do {
            opcion = vista.mostrarMenu();
            switch (opcion) {
                case 1 -> vista.mostrarEquipos(torneo.getEquipos());
                case 2 -> inscribirEquipo();
                case 3 -> eliminarEquipo();
                case 4 -> realizarSorteo();
                case 5 -> verResultados();
                case 6 -> guardar();
                case 7 -> cargar();
                case 8 -> {
                    torneo.restablecerEquipos();
                    vista.mostrarMensaje("Se restablecieron los 16 equipos de la liga.");
                }
                case 0 -> salir();
                default -> vista.mostrarError("La opción " + opcion + " no existe.");
            }
        } while (opcion != 0);
    }

    /** Al arrancar intenta recuperar el último torneo guardado. */
    private void cargarAlIniciar() {
        try {
            torneo = archivo.cargar();
            vista.mostrarMensaje("Se cargó el torneo guardado en " + archivo.getRuta()
                    + " (" + torneo.getEquipos().size() + " equipos).");
        } catch (FileNotFoundException e) {
            torneo = Torneo.conEquiposDeLaLiga();
            vista.mostrarMensaje("No hay un torneo guardado todavía. Se inicia con los 16 equipos de la liga.");
        } catch (IOException | ClassNotFoundException e) {
            torneo = Torneo.conEquiposDeLaLiga();
            vista.mostrarError("No se pudo leer " + archivo.getRuta() + " (" + e.getMessage()
                    + "). Se inicia un torneo nuevo.");
        }
    }

    private void inscribirEquipo() {
        try {
            String nombre = vista.pedirTexto("Nombre del equipo: ");
            String ciudad = vista.pedirTexto("Ciudad: ");
            torneo.agregarEquipo(new Equipo(nombre, ciudad));
            vista.mostrarMensaje(nombre + " inscrito. Ahora hay " + torneo.getEquipos().size() + " equipos.");
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void eliminarEquipo() {
        vista.mostrarEquipos(torneo.getEquipos());
        String nombre = vista.pedirTexto("Nombre del equipo a eliminar: ");
        if (torneo.eliminarEquipo(nombre)) {
            vista.mostrarMensaje(nombre + " eliminado. Quedan " + torneo.getEquipos().size() + " equipos.");
        } else {
            vista.mostrarError("No existe un equipo llamado " + nombre + ".");
        }
    }

    private void realizarSorteo() {
        try {
            torneo.realizarSorteo();
            vista.mostrarSorteo(torneo.getNombre(), torneo.getEtapas(), torneo.getCampeon(),
                    torneo.getFechaSorteo());
        } catch (NumeroEquiposInvalidoException e) {
            vista.mostrarError(e.getMessage());
            vista.mostrarMensaje("Use la opción 2, 3 u 8 para dejar 16, 8, 4 o 2 equipos.");
        } catch (SorteoInvalidoException e) {
            vista.mostrarError("El sorteo no se pudo completar: " + e.getMessage());
        }
    }

    private void verResultados() {
        if (!torneo.isSorteoRealizado()) {
            vista.mostrarMensaje("Todavía no se ha realizado el sorteo (opción 4).");
            return;
        }
        vista.mostrarSorteo(torneo.getNombre(), torneo.getEtapas(), torneo.getCampeon(),
                torneo.getFechaSorteo());
    }

    private void guardar() {
        try {
            archivo.guardar(torneo);
            vista.mostrarMensaje("Torneo guardado en " + archivo.getRuta() + ".");
        } catch (IOException e) {
            vista.mostrarError("No se pudo guardar el torneo: " + e.getMessage());
        }
    }

    private void cargar() {
        try {
            torneo = archivo.cargar();
            vista.mostrarMensaje("Torneo cargado desde " + archivo.getRuta() + ".");
            if (torneo.isSorteoRealizado()) {
                vista.mostrarMensaje("Tiene un sorteo guardado; el campeón fue " + torneo.getCampeon()
                        + ". Use la opción 5 para verlo.");
            }
        } catch (FileNotFoundException e) {
            vista.mostrarError("Todavía no existe el archivo " + archivo.getRuta() + ". Guarde primero (opción 6).");
        } catch (InvalidClassException e) {
            vista.mostrarError("El archivo fue guardado con otra versión del programa: " + e.getMessage());
        } catch (IOException e) {
            vista.mostrarError("El archivo está dañado o no se puede leer: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            vista.mostrarError("El archivo contiene una clase desconocida: " + e.getMessage());
        }
    }

    private void salir() {
        if (vista.confirmar("¿Desea guardar el torneo antes de salir?")) {
            guardar();
        }
        vista.mostrarMensaje("Programa finalizado.");
    }
}
