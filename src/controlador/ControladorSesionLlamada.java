package controlador;

import serverclient.Cliente;
import vista.interfaces.IVistaSesionLlamada;
import vista.vistas.VistaSesionLlamada;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ControladorSesionLlamada implements ActionListener {

    private static ControladorSesionLlamada controladorSesionLlamada = null;

    private IVistaSesionLlamada vista;

    private ControladorSesionLlamada() {
        this.vista = new VistaSesionLlamada();
        this.vista.setActionListener(this);
    }

    public static ControladorSesionLlamada get(boolean mostrar){
        if( controladorSesionLlamada == null ) {
            controladorSesionLlamada = new ControladorSesionLlamada();
        }

        if( mostrar ){
            controladorSesionLlamada.vista.mostrar();
        }

        return controladorSesionLlamada;
    }

    public void esconderVista() {
        this.vista.esconder();
    }

    public void actualizarTitulo(String ip, int puerto) {
        this.vista.actualizarTitulo(ip, puerto);
    }

    public void muestraMensaje(String msg) {
        vista.agregarLineaChat(msg);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String comando = e.getActionCommand();

        String mensaje;

        switch(comando){
            case("Enviar"):
                mensaje = vista.getMensaje();
                vista.limpiarCampo();
                Cliente.getCliente().enviaMensaje(mensaje);
                this.muestraMensaje("Yo: " + mensaje);
            break;

            case("Desconectar"):
				Cliente.getCliente().enviaMensaje("DESCONECTAR");
                ControladorSesionLlamada.get(false).esconderVista();
                ControladorInicioNuevo.get(true);
            break;
        }
    }

}