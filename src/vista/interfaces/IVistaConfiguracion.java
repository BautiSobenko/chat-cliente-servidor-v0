package vista.interfaces;

import java.awt.event.ActionListener;

public interface IVistaConfiguracion {

	public void setActionListener(ActionListener controlador);
	public String getIP();
	public int getPuerto();
	public void mostrar();
	public void esconder();
	void lanzarVentanaEmergente(String mensaje);
}
