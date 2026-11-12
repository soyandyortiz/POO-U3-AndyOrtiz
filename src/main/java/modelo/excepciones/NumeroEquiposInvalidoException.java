package modelo.excepciones;

/**
 * Se lanza cuando la cantidad de equipos de una etapa no sirve para
 * un torneo de eliminación directa, es decir, cuando no es una potencia
 * de 2 (16, 8, 4, 2) o cuando no hay equipos suficientes.
 */
public class NumeroEquiposInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int cantidad;

    public NumeroEquiposInvalidoException(String mensaje) {
        super(mensaje);
        this.cantidad = -1;
    }

    public NumeroEquiposInvalidoException(String mensaje, int cantidad) {
        super(mensaje);
        this.cantidad = cantidad;
    }

    /** Cantidad de equipos que provocó el error (-1 si no se indicó). */
    public int getCantidad() {
        return cantidad;
    }
}
