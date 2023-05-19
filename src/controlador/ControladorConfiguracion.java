package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.UnknownHostException;

import configuracion.ConfiguracionCliente;
import vista.interfaces.IVistaConfiguracion;
import configuracion.Configuracion;
import vista.vistas.VistaConfiguracionPuerto;


public class ControladorConfiguracion implements ActionListener, WindowListener {

	private static ControladorConfiguracion controladorConfiguracion = null;

	private IVistaConfiguracion vista;

	private ControladorConfiguracion() {
		this.vista = new VistaConfiguracionPuerto();
		this.vista.setActionListener(this);
		this.vista.setWindowListener(this);
	}

    public static ControladorConfiguracion get(boolean mostrar){
        if( controladorConfiguracion == null ) {
			controladorConfiguracion = new ControladorConfiguracion();
		}

		if( mostrar ){
			controladorConfiguracion.vista.mostrar();
			controladorConfiguracion.vista.setTxtPuerto(String.valueOf(ConfiguracionCliente.getConfig().getParametros()[1]));
		}

		return controladorConfiguracion;
    }

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			ControladorInicioNuevo controladorInicio = ControladorInicioNuevo.get(true);

			String IP = "localhost";

			if(controladorInicio.getMiPuerto() != vista.getPuerto() ){

				int miPuerto = vista.getPuerto();

				Configuracion configuracion = ConfiguracionCliente.getConfig(IP, miPuerto);

				if (configuracion.validarConfiguracion()){

					configuracion.setIp(IP);
					configuracion.setPuerto(miPuerto);

					configuracion.escribirArchivoConfiguracion();

					controladorInicio.setMiPuerto(miPuerto);
					controladorInicio.startCliente();
					controladorInicio.verificarBoton();

					this.vista.esconder();
				} else
					vista.lanzarVentanaEmergente("Error al ingresar IP o Puerto");

			} else
				this.vista.esconder();

		}catch (RuntimeException exception){
			vista.lanzarVentanaEmergente("El puerto ingresado ya esta en uso");
		}catch (UnknownHostException exception){

		} catch (Exception ex) {
			vista.lanzarVentanaEmergente("Error al escribir archivo de configuracion");
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		ControladorInicioNuevo.get(true);
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
