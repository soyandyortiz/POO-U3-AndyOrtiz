package modelo.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import modelo.Torneo;

/**
 * Se encarga de guardar y cargar el torneo en un archivo binario
 * usando serialización de objetos.
 */
public class ArchivoTorneo {

    public static final String ARCHIVO_POR_DEFECTO = "torneo.ser";

    private final String ruta;

    public ArchivoTorneo() {
        this(ARCHIVO_POR_DEFECTO);
    }

    public ArchivoTorneo(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return ruta;
    }

    public boolean existe() {
        return new File(ruta).isFile();
    }

    /**
     * Serializa el torneo completo (equipos, partidos y campeón) en el archivo.
     * Se usa try-with-resources para que el flujo se cierre aunque ocurra un error.
     */
    public void guardar(Torneo torneo) throws IOException {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta))) {
            salida.writeObject(torneo);
        }
    }

    /**
     * Lee el archivo y reconstruye el objeto Torneo.
     *
     * @throws java.io.FileNotFoundException si el archivo no existe
     * @throws IOException si el archivo está dañado o no se puede leer
     * @throws ClassNotFoundException si el archivo tiene una clase que no existe en el programa
     */
    public Torneo cargar() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta))) {
            Object leido = entrada.readObject();
            if (!(leido instanceof Torneo)) {
                throw new IOException("El archivo " + ruta + " no contiene un torneo");
            }
            return (Torneo) leido;
        }
    }
}
