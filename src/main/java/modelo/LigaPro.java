package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Los 16 equipos con los que arranca la liga profesional.
 * Se usan los clubes de la LigaPro de Ecuador.
 */
public final class LigaPro {

    private LigaPro() {
    }

    public static List<Equipo> equiposIniciales() {
        List<Equipo> lista = new ArrayList<>();
        lista.add(new Equipo("Barcelona SC", "Guayaquil"));
        lista.add(new Equipo("Emelec", "Guayaquil"));
        lista.add(new Equipo("Liga de Quito", "Quito"));
        lista.add(new Equipo("Independiente del Valle", "Sangolquí"));
        lista.add(new Equipo("Aucas", "Quito"));
        lista.add(new Equipo("Universidad Católica", "Quito"));
        lista.add(new Equipo("El Nacional", "Quito"));
        lista.add(new Equipo("Deportivo Cuenca", "Cuenca"));
        lista.add(new Equipo("Orense", "Machala"));
        lista.add(new Equipo("Delfín", "Manta"));
        lista.add(new Equipo("Técnico Universitario", "Ambato"));
        lista.add(new Equipo("Mushuc Runa", "Ambato"));
        lista.add(new Equipo("Macará", "Ambato"));
        lista.add(new Equipo("Libertad", "Loja"));
        lista.add(new Equipo("Imbabura", "Ibarra"));
        lista.add(new Equipo("Vinotinto", "Quito"));
        return lista;
    }
}
