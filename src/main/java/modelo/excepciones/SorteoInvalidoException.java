package modelo.excepciones;

/**
 * Se lanza desde el método recursivo del sorteo cuando aparece una
 * situación imposible: un equipo queda sin rival, un equipo saldría
 * contra sí mismo o un equipo aparece dos veces en la misma etapa.
 */
public class SorteoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public SorteoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public SorteoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
