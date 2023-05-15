package serverclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface IConexion {

    public void establecerConexion(Object... args);

    public void aceptarConexion();

    public void cerrarConexion();

    public void cerrarServer();

    public void crearConexionEnvio(Object... args);

    public ObjectInputStream getInputStreamConexion();

    public ObjectOutputStream getOutputStreamConexion();

}
