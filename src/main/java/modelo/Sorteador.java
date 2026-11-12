package modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import modelo.excepciones.NumeroEquiposInvalidoException;
import modelo.excepciones.SorteoInvalidoException;

/**
 * Contiene el algoritmo del sorteo. El emparejamiento de una etapa se hace
 * de forma recursiva: en cada llamada se sacan dos equipos al azar del
 * "bombo", se forma el partido y se vuelve a llamar con los que quedan,
 * hasta que el bombo queda vacío (caso base).
 */
public class Sorteador {

    private final Random random;

    public Sorteador() {
        this(new Random());
    }

    /** Permite pasar un Random con semilla para poder repetir un sorteo en las pruebas. */
    public Sorteador(Random random) {
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * Revisa que la cantidad de equipos sirva para eliminación directa:
     * debe ser potencia de 2 y estar entre 2 y 16.
     */
    public void validarCantidad(int cantidad) throws NumeroEquiposInvalidoException {
        if (cantidad < 2) {
            throw new NumeroEquiposInvalidoException(
                    "Se necesitan al menos 2 equipos para sortear y hay " + cantidad, cantidad);
        }
        // un número es potencia de 2 si en binario tiene un solo bit en 1
        if ((cantidad & (cantidad - 1)) != 0) {
            throw new NumeroEquiposInvalidoException("Hay " + cantidad
                    + " equipos y ese número no es potencia de 2 (16, 8, 4 o 2), "
                    + "así no se puede armar un torneo de eliminación directa", cantidad);
        }
        if (Etapa.segunCantidad(cantidad) == null) {
            throw new NumeroEquiposInvalidoException(
                    "La liga admite como máximo 16 equipos y hay " + cantidad, cantidad);
        }
    }

    /**
     * Sortea los partidos de una etapa.
     */
    public List<Partido> sortearEtapa(List<Equipo> equipos, Etapa etapa)
            throws NumeroEquiposInvalidoException, SorteoInvalidoException {
        validarCantidad(equipos.size());
        List<Equipo> bombo = new ArrayList<>(equipos); // copia para no dañar la lista original
        List<Partido> partidos = new ArrayList<>();
        emparejar(bombo, partidos, new HashSet<>(), etapa);
        return partidos;
    }

    /**
     * Método recursivo del sorteo.
     *
     * @param bombo      equipos que todavía no tienen rival
     * @param partidos   partidos que ya se formaron en esta etapa
     * @param yaSorteados equipos que ya tienen partido en esta etapa
     * @param etapa      etapa que se está sorteando
     * @throws SorteoInvalidoException si el sorteo llega a una situación imposible
     */
    public void emparejar(List<Equipo> bombo, List<Partido> partidos, Set<Equipo> yaSorteados, Etapa etapa)
            throws SorteoInvalidoException {
        // caso base: ya no quedan equipos por emparejar
        if (bombo.isEmpty()) {
            return;
        }
        if (bombo.size() == 1) {
            throw new SorteoInvalidoException(
                    "El equipo " + bombo.get(0) + " se quedó sin rival en " + etapa.getNombre());
        }

        // se saca un equipo al azar como local y otro como visitante
        Equipo local = bombo.remove(random.nextInt(bombo.size()));
        Equipo visitante = bombo.remove(random.nextInt(bombo.size()));

        if (local.equals(visitante)) {
            throw new SorteoInvalidoException(
                    local + " no puede ser competidor y oponente en el mismo partido");
        }
        if (!yaSorteados.add(local)) {
            throw new SorteoInvalidoException(local + " ya tiene partido en " + etapa.getNombre());
        }
        if (!yaSorteados.add(visitante)) {
            throw new SorteoInvalidoException(visitante + " ya tiene partido en " + etapa.getNombre());
        }

        partidos.add(new Partido(etapa, local, visitante));

        // llamada recursiva con los equipos que quedan en el bombo
        emparejar(bombo, partidos, yaSorteados, etapa);
    }
}
