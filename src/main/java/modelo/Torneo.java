package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import modelo.excepciones.NumeroEquiposInvalidoException;
import modelo.excepciones.SorteoInvalidoException;

/**
 * Clase principal del modelo: guarda los equipos inscritos y los partidos
 * de cada etapa. Implementa Serializable para poder guardarse en torneo.ser.
 */
public class Torneo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private final ArrayList<Equipo> equipos = new ArrayList<>();
    private final LinkedHashMap<Etapa, List<Partido>> etapas = new LinkedHashMap<>();
    private Equipo campeon;
    private LocalDateTime fechaSorteo;

    // el sorteador no se guarda en el archivo, se vuelve a crear cuando hace falta
    private transient Sorteador sorteador;

    public Torneo(String nombre) {
        this.nombre = nombre;
    }

    /** Crea el torneo con los 16 equipos iniciales de la liga. */
    public static Torneo conEquiposDeLaLiga() {
        Torneo t = new Torneo("LigaPro Ecuador - Copa de eliminación");
        for (Equipo e : LigaPro.equiposIniciales()) {
            t.agregarEquipo(e);
        }
        return t;
    }

    // ------------------------------------------------ equipos

    public void agregarEquipo(Equipo equipo) {
        if (equipos.contains(equipo)) {
            throw new IllegalArgumentException("El equipo " + equipo + " ya está inscrito");
        }
        equipos.add(equipo);
        borrarResultados(); // si cambian los equipos, el sorteo anterior ya no vale
    }

    public boolean eliminarEquipo(String nombreEquipo) {
        boolean eliminado = equipos.removeIf(e -> e.getNombre().equalsIgnoreCase(nombreEquipo.trim()));
        if (eliminado) {
            borrarResultados();
        }
        return eliminado;
    }

    public void restablecerEquipos() {
        equipos.clear();
        equipos.addAll(LigaPro.equiposIniciales());
        borrarResultados();
    }

    // ------------------------------------------------ sorteo

    /**
     * Realiza el sorteo completo, desde la primera etapa hasta la final.
     */
    public void realizarSorteo() throws NumeroEquiposInvalidoException, SorteoInvalidoException {
        getSorteador().validarCantidad(equipos.size());
        borrarResultados();
        jugarEtapas(new ArrayList<>(equipos));
        fechaSorteo = LocalDateTime.now();
    }

    /**
     * Recorre las etapas también de forma recursiva: sortea y juega la etapa
     * actual y vuelve a llamarse con los ganadores, hasta que queda un campeón.
     */
    private void jugarEtapas(List<Equipo> clasificados)
            throws NumeroEquiposInvalidoException, SorteoInvalidoException {
        if (clasificados.size() == 1) { // caso base: ya hay campeón
            campeon = clasificados.get(0);
            return;
        }
        Etapa etapa = Etapa.segunCantidad(clasificados.size());
        if (etapa == null) {
            throw new NumeroEquiposInvalidoException(
                    "No existe una etapa para " + clasificados.size() + " equipos", clasificados.size());
        }

        List<Partido> partidos = getSorteador().sortearEtapa(clasificados, etapa);
        List<Equipo> ganadores = new ArrayList<>();
        for (Partido p : partidos) {
            p.jugar(getSorteador().getRandom());
            ganadores.add(p.getGanador());
        }
        etapas.put(etapa, partidos);

        jugarEtapas(ganadores);
    }

    private void borrarResultados() {
        etapas.clear();
        campeon = null;
        fechaSorteo = null;
    }

    private Sorteador getSorteador() {
        if (sorteador == null) {
            sorteador = new Sorteador();
        }
        return sorteador;
    }

    /** Se usa en las pruebas para fijar la semilla del sorteo. */
    public void setSorteador(Sorteador sorteador) {
        this.sorteador = sorteador;
    }

    // ------------------------------------------------ consultas

    public boolean isSorteoRealizado() {
        return campeon != null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Equipo> getEquipos() {
        return Collections.unmodifiableList(equipos);
    }

    public Map<Etapa, List<Partido>> getEtapas() {
        return Collections.unmodifiableMap(etapas);
    }

    public Equipo getCampeon() {
        return campeon;
    }

    public LocalDateTime getFechaSorteo() {
        return fechaSorteo;
    }
}
