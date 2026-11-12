package modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa a un equipo de la liga.
 * Dos equipos se consideran iguales si tienen el mismo nombre
 * (sin importar mayúsculas), así se evita inscribir dos veces al mismo club.
 */
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String ciudad;

    public Equipo(String nombre, String ciudad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacío");
        }
        this.nombre = nombre.trim();
        this.ciudad = (ciudad == null || ciudad.isBlank()) ? "Sin ciudad" : ciudad.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipo)) {
            return false;
        }
        Equipo otro = (Equipo) o;
        return nombre.equalsIgnoreCase(otro.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }

    @Override
    public String toString() {
        return nombre;
    }
}
