package vista.interfaces;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public interface IVistaInicio {

    void setActionListener(ActionListener controlador);

    void setWindowListener(WindowListener controlador);

    String getIP();
    int getPuerto();
    void lanzarVentanaEmergente(String mensaje);
    void limpiarCampo();
    public void mostrar();
    public void esconder();
    public void error(String mensaje);
    void deshabilitarBotonConexion();
    void habilitarBotonConexion();
    void tituloInstancia(String ipOrigen, int miPuerto);
}
