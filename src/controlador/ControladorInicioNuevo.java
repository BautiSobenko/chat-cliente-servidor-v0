package controlador;

import configuracion.Configuracion;
import serverclient.Cliente;
import vista.interfaces.IVistaInicio;
import vista.vistas.VistaInicio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ControladorInicioNuevo implements ActionListener {

    private static ControladorInicioNuevo controladorInicio = null;

    private IVistaInicio vista;
    private Cliente cliente;
    private int miPuerto;
    private Thread hiloCliente;

    private ControladorSesionLlamada controladorSesionLlamada;

    private ControladorInicioNuevo() {
        this.vista = new VistaInicio();
        this.vista.setActionListener(this);
    }

    public void startCliente() {
        this.cliente = Cliente.getCliente();
        //TODO: Debemos obtener el puerto y la IP del servidor de otro lado, no hardcodearlo
        this.cliente.setPuertoServidor(9090);
        this.cliente.setIpServer("localhost");

        this.cliente.setPuertoOrigen(miPuerto); //Lo seteo para evitar problemas en el ServerSocket en el run()
        this.hiloCliente = new Thread(this.cliente);
        hiloCliente.start();
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
                        System.out.println(ipDestino + puertoDestino);
                        this.cliente.setIpOrigen("localhost");
                        this.cliente.setPuertoOrigen(miPuerto);

                        this.cliente.enviaMensaje("LLAMADA");

                        this.lanzarAviso("Esperando a ser atendido...");

                    }catch (Exception exception){
                        exception.printStackTrace();
	                }
                }
                else
                	this.vista.error("No se puede comunicar con su mismo puerto");
                break;
        }

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

}
