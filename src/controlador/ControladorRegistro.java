package controlador;

import configuracion.Configuracion;
import vista.interfaces.IVistaConfiguracion;
import vista.vistas.VistaConfiguracionPuerto;
import vista.vistas.VistaRegistro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControladorRegistro implements ActionListener {

        private static ControladorRegistro controladorRegistro = null;

        private IVistaConfiguracion vista;

        private ControladorRegistro() {
            this.vista = new VistaRegistro();
            this.vista.setActionListener(this);

            //Actualizo label con mi IP
            InetAddress adress = null;
            try {
                adress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            String ipOrigen = adress.getHostAddress();
            this.vista.setLblDireccionIP(ipOrigen);
        }

        public static ControladorRegistro get(boolean mostrar){
            if( controladorRegistro == null ) {
                controladorRegistro = new ControladorRegistro();
            }

            if( mostrar ){
                controladorRegistro.vista.mostrar();
            }

            return controladorRegistro;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            try {

                ControladorInicioNuevo controladorInicio = ControladorInicioNuevo.get(true);

                String IP = this.vista.getIP();
                int miPuerto = this.vista.getPuerto();

                Configuracion configuracion = Configuracion.getConfig(IP, miPuerto);

                if(controladorInicio.getMiPuerto() != vista.getPuerto() ) {

                    if (configuracion.validarConfiguracion()) {

                        configuracion.escribirArchivoConfiguracion();

                        controladorInicio.setMiPuerto(miPuerto);
                        controladorInicio.startCliente();
                        controladorInicio.verificarBoton();

                        this.vista.esconder();
                    } else {
                        vista.lanzarVentanaEmergente("Error al ingresar IP o Puerto");
                    }
                }
            }catch (RuntimeException exception){
                vista.lanzarVentanaEmergente("El puerto ingresado ya esta en uso");
            }catch (UnknownHostException ignored){

            } catch (Exception ex) {
                vista.lanzarVentanaEmergente("Error al escribir archivo de configuracion");
            }
        }



}
