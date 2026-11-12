package modelo;

/**
 * Etapas del torneo de eliminación directa.
 * Cada etapa sabe cuántos equipos deben llegar a ella.
 */
public enum Etapa {
    OCTAVOS("Octavos de final", 16),
    CUARTOS("Cuartos de final", 8),
    SEMIFINAL("Semifinales", 4),
    FINAL("Final", 2);

    private final String nombre;
    private final int equipos;

    Etapa(String nombre, int equipos) {
        this.nombre = nombre;
        this.equipos = equipos;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEquipos() {
        return equipos;
    }

    /** Devuelve la etapa que corresponde a cierta cantidad de equipos, o null si no existe. */
    public static Etapa segunCantidad(int cantidadEquipos) {
        for (Etapa e : values()) {
            if (e.equipos == cantidadEquipos) {
                return e;
            }
        }
        return null;
    }
}
