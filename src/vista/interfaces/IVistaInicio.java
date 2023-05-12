package vista.interfaces;

import java.awt.event.ActionListener;

public interface IVistaInicio {

    void setActionListener(ActionListener controlador);
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
