package modelo;

import java.io.Serializable;
import java.util.Random;

/**
 * Un partido entre dos equipos dentro de una etapa.
 * Como es eliminación directa, si hay empate se define por penales.
 */
public class Partido implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Etapa etapa;
    private final Equipo local;
    private final Equipo visitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean porPenales;
    private Equipo ganador;

    public Partido(Etapa etapa, Equipo local, Equipo visitante) {
        if (local == null || visitante == null) {
            throw new IllegalArgumentException("Un partido necesita dos equipos");
        }
        if (local.equals(visitante)) {
            throw new IllegalArgumentException(local + " no puede jugar contra sí mismo");
        }
        this.etapa = etapa;
        this.local = local;
        this.visitante = visitante;
    }

    /** Simula el resultado del partido con goles aleatorios (0 a 4). */
    public void jugar(Random random) {
        golesLocal = random.nextInt(5);
        golesVisitante = random.nextInt(5);
        if (golesLocal > golesVisitante) {
            ganador = local;
        } else if (golesVisitante > golesLocal) {
            ganador = visitante;
        } else {
            porPenales = true;
            ganador = random.nextBoolean() ? local : visitante;
        }
    }

    public boolean participa(Equipo equipo) {
        return local.equals(equipo) || visitante.equals(equipo);
    }

    public boolean isJugado() {
        return ganador != null;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public Equipo getLocal() {
        return local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public boolean isPorPenales() {
        return porPenales;
    }

    public Equipo getGanador() {
        return ganador;
    }

    @Override
    public String toString() {
        if (!isJugado()) {
            return local + " vs " + visitante;
        }
        return local + " " + golesLocal + " - " + golesVisitante + " " + visitante
                + (porPenales ? " (penales)" : "") + "  -> pasa " + ganador;
    }
}
