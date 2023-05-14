package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import vista.interfaces.IVistaConfiguracion;
import configuracion.Configuracion;
import vista.vistas.VistaConfiguracionPuerto;


public class ControladorConfiguracion implements ActionListener{

	private static ControladorConfiguracion controladorConfiguracion = null;

	private IVistaConfiguracion vista;

	private ControladorConfiguracion() {
		this.vista = new VistaConfiguracionPuerto();
		this.vista.setActionListener(this);
	}

    public static ControladorConfiguracion get(boolean mostrar){
        if( controladorConfiguracion == null ) {
			controladorConfiguracion = new ControladorConfiguracion();
		}

		if( mostrar )
			controladorConfiguracion.vista.mostrar();

		return controladorConfiguracion;
    }

	@Override
	public void actionPerformed(ActionEvent e) {

		try {

			ControladorInicioNuevo controladorInicio = ControladorInicioNuevo.get(true);

			String IP = "localhost";
			int miPuerto = vista.getPuerto();

			//Configuracion.getConfig().getPuerto();
			//if (Configuracion.puertoValido()) {}
				controladorInicio.setMiPuerto(miPuerto);

				controladorInicio.startCliente();
				controladorInicio.verificarBoton();
				this.vista.esconder();
		//} catch (IOException exception) {
		//	vista.lanzarVentanaEmergente("El puerto ingresado ya esta en uso");
		}catch (Exception exception){
			vista.lanzarVentanaEmergente("Error al ingresar Puerto");

		}
	}

}
