package controlador;

import serverclient.Cliente;
import vista.interfaces.IVistaInicio;
import vista.vistas.VistaInicio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControladorInicioNuevo implements ActionListener, WindowListener {

    private static ControladorInicioNuevo controladorInicio = null;

    private IVistaInicio vista;
    private Cliente cliente;
    private int miPuerto;
    private Thread hiloCliente;

    private ControladorSesionLlamada controladorSesionLlamada;

    private ControladorInicioNuevo() {
        this.vista = new VistaInicio();
        this.vista.setActionListener(this);
        this.vista.setWindowListener(this);
    }

    public void startCliente() {
        this.cliente = Cliente.getCliente();

        this.cliente.setPuertoServidor(9090);
        this.cliente.setIpServer("localhost");

        this.cliente.setPuertoOrigen(miPuerto); //Lo seteo para evitar problemas en el ServerSocket en el run()

        this.hiloCliente = new Thread(this.cliente);
        hiloCliente.start();

        this.cliente.enviaMensaje("REGISTRO");

    }

    public static ControladorInicioNuevo get(boolean mostrar) {
        if( controladorInicio == null) {
            controladorInicio = new ControladorInicioNuevo();
        }
        if( mostrar ) {
            controladorInicio.vista.mostrar();
        }else
            controladorInicio.vista.esconder();


        return controladorInicio;
    }

    public int getMiPuerto() {

        return miPuerto;
    }

    public void lanzarAviso(String msg){
        this.vista.lanzarVentanaEmergente(msg);
    }

    public void error(String msg) {
        this.vista.error(msg);
    }

    public void limpiarCampos(){
        this.vista.limpiarCampo();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String comando = e.getActionCommand();

        switch (comando) {
            case "Configuracion":
                ControladorConfiguracion.get(true);
                this.vista.esconder();
            break;

            case "Conectar":
                String ipDestino = vista.getIP();
                int puertoDestino = vista.getPuerto();

                if( puertoDestino != this.miPuerto ) {
	                this.limpiarCampos();
	                try {
                        this.cliente.setIpDestino(ipDestino);
                        this.cliente.setPuertoDestino(puertoDestino);
                        this.cliente.setIpOrigen("localhost");
                        this.cliente.setPuertoOrigen(miPuerto);

                        this.cliente.enviaMensaje("LLAMADA");

                        this.lanzarAviso("Esperando a ser atendido...");

                    }catch (Exception exception){
                        this.vista.error("Error en la conexion");
	                }
                }
                else
                	this.vista.error("No se puede comunicar con su mismo puerto");
                break;
        }

    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setMiPuerto(int puerto) {
    	this.miPuerto = puerto;
        actualizarTituloVista();
    }

    public void actualizarTituloVista(){
        InetAddress adress; //Obtengo la ip origen (Informacion extra)

        try {
            adress = InetAddress.getLocalHost();
            String ipOrigen = adress.getHostAddress();
            this.vista.tituloInstancia(ipOrigen, miPuerto);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    public void verificarBoton(){
      //  if (Configuracion.puertoValido())
      //      this.vista.habilitarBotonConexion();
      //  else
      //      this.vista.deshabilitarBotonConexion();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.cliente.enviaMensaje("ELIMINA REGISTRO");
        System.exit(0);
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
