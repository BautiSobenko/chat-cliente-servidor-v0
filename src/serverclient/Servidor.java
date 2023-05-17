package serverclient;

import controlador.ControladorInicioNuevo;
import mensaje.Mensaje;
import vista.vistas.VistaServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Servidor implements Runnable, Recepcion, Emision {

    private int puertoServer;
    private static Servidor servidor = null;
    private VistaServidor vistaServidor;
    private HashSet<String> conexiones;

    private HashMap<String, Integer> registro;

    private Conexion conexion;

    private Servidor(){
        this.puertoServer = 9090; //Si no lo seteo antes, se rompe, intenta abrir ServerSocket con puerto des escucha null
        this.vistaServidor = new VistaServidor();

        conexiones = new HashSet<>();
        registro = new HashMap<>();

        Thread hiloServer = new Thread(this);
        hiloServer.start();


    }

    public static Servidor getServer(){
        if( servidor == null ){
            servidor = new Servidor();
        }
        return servidor;
    }


    @Override
    public void run() {

        try {

            this.conexion = new Conexion();

            //ServerSocket
            conexion.establecerConexion(this.puertoServer);

            this.vistaServidor.muestraMensaje("Servidor Iniciado! \nPuerto: " + this.puertoServer + "\n");

            String ipOrigen, ipDestino, msg;
            int puertoDestino;
            int puertoOrigen;
            Mensaje mensaje;

            while (true) {

                //Acepto conexion, me crea un Socket
                conexion.aceptarConexion();

                mensaje = this.recibeMensaje();

                puertoDestino = mensaje.getPuertoDestino();
                puertoOrigen = mensaje.getPuertoOrigen();
                ipOrigen = mensaje.getIpOrigen();
                ipDestino = mensaje.getIpDestino();
                msg = mensaje.getMensaje();

                if( msg.equalsIgnoreCase("LLAMADA") && this.conexiones.contains(ipDestino) )
                    ControladorInicioNuevo.get(false).error("No es posible conectar. Ocupado");
                else{

                    if( msg.equalsIgnoreCase("LLAMADA ACEPTADA") ){
                        this.conexiones.add(ipOrigen);
                        this.conexiones.add(ipDestino);
                    }
                    else if( msg.equalsIgnoreCase("DESCONECTAR") ){
                        this.conexiones.remove(ipOrigen);
                        this.conexiones.remove(ipDestino);
                    }
                    else if( msg.equalsIgnoreCase("REGISTRO") ) {

                        if( this.registrarCliente(ipOrigen, puertoOrigen) )
                            msg = "REGISTRO EXITOSO";
                        else
                            msg = "REGISTRO FALLIDO";

                        mensaje.setMensaje(msg);
                    }

                    this.vistaServidor.muestraMensaje("ORIGEN: " + ipOrigen + " => DESTINO: " + ipDestino + " :\n" + msg + "\n\n");

                    //Creo socket: Reenvio del mensaje
                    this.conexion.crearConexionEnvio(ipDestino, puertoDestino);

                    ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

                    out.writeObject(mensaje);
                }

                this.conexion.cerrarConexion();

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void enviaMensaje(String msg) {

    }

    public boolean registrarCliente(String ipOrigen, int puertoOrigen) {

        boolean existeRegistro = false;

        for (Map.Entry<String, Integer> entry : this.registro.entrySet()) {
            if( entry.getKey().equalsIgnoreCase(ipOrigen) && entry.getValue() == puertoOrigen) {
                existeRegistro = true;
                break;
            }
        }

        if( existeRegistro ){
            return false;
        }else{
            this.registro.put(ipOrigen, puertoOrigen);
            this.vistaServidor.muestraMensaje("REGISTRO: " + ipOrigen + " | " + puertoOrigen + "\n\n");

            return true;

        }

    }

    @Override
    public Mensaje recibeMensaje() {
        ObjectInputStream in = conexion.getInputStreamConexion();
        try {
            return (Mensaje) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
