package pruebas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import modelo.Equipo;
import modelo.Etapa;
import modelo.LigaPro;
import modelo.Partido;
import modelo.Sorteador;
import modelo.Torneo;
import modelo.excepciones.NumeroEquiposInvalidoException;
import modelo.excepciones.SorteoInvalidoException;
import modelo.persistencia.ArchivoTorneo;

/**
 * Pruebas del sorteo, de las excepciones y de la serialización.
 * Se ejecuta como Java Application y muestra [OK] o [FALLA] en cada caso.
 */
public class PruebasTorneo {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== PRUEBAS DEL SORTEO DE LA LIGA ===");

        // 1. sorteo completo con 16 equipos
        Torneo t = Torneo.conEquiposDeLaLiga();
        t.realizarSorteo();
        Map<Etapa, List<Partido>> etapas = t.getEtapas();
        verificar("Se juegan las 4 etapas (octavos, cuartos, semifinal y final)", etapas.size() == 4);
        verificar("Octavos 8, cuartos 4, semifinales 2 y final 1 partido",
                etapas.get(Etapa.OCTAVOS).size() == 8 && etapas.get(Etapa.CUARTOS).size() == 4
                        && etapas.get(Etapa.SEMIFINAL).size() == 2 && etapas.get(Etapa.FINAL).size() == 1);
        verificar("Hay un campeón y es uno de los equipos inscritos",
                t.getCampeon() != null && t.getEquipos().contains(t.getCampeon()));

        // 2. reglas del sorteo en cada etapa
        boolean sinRepetidos = true;
        boolean sinAutoPartido = true;
        for (List<Partido> partidos : etapas.values()) {
            Set<Equipo> vistos = new HashSet<>();
            for (Partido p : partidos) {
                sinRepetidos &= vistos.add(p.getLocal()) && vistos.add(p.getVisitante());
                sinAutoPartido &= !p.getLocal().equals(p.getVisitante());
            }
        }
        verificar("Ningún equipo repite partido en la misma etapa", sinRepetidos);
        verificar("Ningún equipo es competidor y oponente a la vez", sinAutoPartido);

        // 3. a cada etapa llegan solo los ganadores de la anterior
        Set<Equipo> ganadoresOctavos = new HashSet<>();
        for (Partido p : etapas.get(Etapa.OCTAVOS)) {
            ganadoresOctavos.add(p.getGanador());
        }
        Set<Equipo> enCuartos = new HashSet<>();
        for (Partido p : etapas.get(Etapa.CUARTOS)) {
            enCuartos.add(p.getLocal());
            enCuartos.add(p.getVisitante());
        }
        verificar("A cuartos llegan exactamente los 8 ganadores de octavos", ganadoresOctavos.equals(enCuartos));

        // 4. aleatoriedad: con semillas distintas salen cruces distintos
        Set<String> cruces = new HashSet<>();
        for (int semilla = 0; semilla < 20; semilla++) {
            Torneo otro = Torneo.conEquiposDeLaLiga();
            otro.setSorteador(new Sorteador(new Random(semilla)));
            otro.realizarSorteo();
            cruces.add(otro.getEtapas().get(Etapa.OCTAVOS).get(0).toString());
        }
        verificar("El sorteo es aleatorio (20 sorteos dan cruces diferentes)", cruces.size() > 1);

        // 5. NumeroEquiposInvalidoException
        Torneo conDoce = Torneo.conEquiposDeLaLiga();
        for (int i = 0; i < 4; i++) {
            conDoce.eliminarEquipo(conDoce.getEquipos().get(0).getNombre());
        }
        verificar("12 equipos lanza NumeroEquiposInvalidoException",
                lanza(NumeroEquiposInvalidoException.class, conDoce::realizarSorteo));
        verificar("0 equipos lanza NumeroEquiposInvalidoException",
                lanza(NumeroEquiposInvalidoException.class, () -> new Sorteador().validarCantidad(0)));
        verificar("32 equipos lanza NumeroEquiposInvalidoException (máximo 16)",
                lanza(NumeroEquiposInvalidoException.class, () -> new Sorteador().validarCantidad(32)));
        verificar("8, 4 y 2 equipos son válidos", !lanza(NumeroEquiposInvalidoException.class, () -> {
            Sorteador s = new Sorteador();
            s.validarCantidad(8);
            s.validarCantidad(4);
            s.validarCantidad(2);
        }));

        // 6. SorteoInvalidoException desde el método recursivo
        List<Equipo> impares = new ArrayList<>(LigaPro.equiposIniciales().subList(0, 3));
        verificar("Un equipo sin rival lanza SorteoInvalidoException",
                lanza(SorteoInvalidoException.class, () -> new Sorteador()
                        .emparejar(impares, new ArrayList<>(), new HashSet<>(), Etapa.SEMIFINAL)));
        List<Equipo> repetido = new ArrayList<>(LigaPro.equiposIniciales().subList(0, 3));
        repetido.add(repetido.get(0));
        verificar("Un equipo repetido en la etapa lanza SorteoInvalidoException",
                lanza(SorteoInvalidoException.class, () -> new Sorteador().sortearEtapa(repetido, Etapa.SEMIFINAL)));

        // 7. serialización
        File temporal = File.createTempFile("torneo_prueba", ".ser");
        temporal.deleteOnExit();
        ArchivoTorneo archivo = new ArchivoTorneo(temporal.getPath());
        archivo.guardar(t);
        Torneo leido = archivo.cargar();
        verificar("Guardar y cargar conserva el campeón",
                leido.getCampeon() != null && leido.getCampeon().equals(t.getCampeon()));
        verificar("Guardar y cargar conserva los 15 partidos",
                leido.getEtapas().values().stream().mapToInt(List::size).sum() == 15);
        verificar("Cargar un archivo que no existe lanza FileNotFoundException",
                lanza(FileNotFoundException.class, () -> new ArchivoTorneo("no_existe.ser").cargar()));
        File basura = File.createTempFile("basura", ".ser");
        basura.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(basura)) {
            fos.write("esto no es un torneo".getBytes());
        }
        verificar("Cargar un archivo dañado lanza IOException",
                lanza(IOException.class, () -> new ArchivoTorneo(basura.getPath()).cargar()));

        System.out.println("-------------------------------------------");
        System.out.println("Aprobadas: " + aprobadas + " | Fallidas: " + fallidas);
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            aprobadas++;
            System.out.println("[OK]    " + descripcion);
        } else {
            fallidas++;
            System.out.println("[FALLA] " + descripcion);
        }
    }

    /** Acción que puede lanzar cualquier excepción (para probar los casos de error). */
    private interface Accion {
        void ejecutar() throws Exception;
    }

    private static boolean lanza(Class<? extends Exception> tipo, Accion accion) {
        try {
            accion.ejecutar();
            return false;
        } catch (Exception e) {
            return tipo.isInstance(e);
        }
    }
}
