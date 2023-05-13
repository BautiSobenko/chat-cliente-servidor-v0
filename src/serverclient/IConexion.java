package serverclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface IConexion {

    public void establecerConexion(Object... args);

    public void aceptarConexion();

    public void cerrarConexion();

    public ObjectInputStream getInputStreamConexion();

    public ObjectOutputStream getOutputStreamConexion();

}
