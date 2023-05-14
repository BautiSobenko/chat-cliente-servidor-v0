package serverclient;

import controlador.ControladorInicioNuevo;
import mensaje.Mensaje;
import vista.vistas.VistaServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

public class Servidor implements Runnable {

    private int puertoServer;
    private static Servidor servidor = null;
    private VistaServidor vistaServidor;
    private HashSet<Integer> conexiones;

    private Conexion conexion;

    private Servidor(){
        this.puertoServer = 9090; //Si no lo seteo antes, se rompe, intenta abrir ServerSocket con puerto des escucha null
        this.vistaServidor = new VistaServidor();
        Thread hiloServer = new Thread(this);
        hiloServer.start();
        conexiones = new HashSet<>();
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

                ObjectInputStream in = new ObjectInputStream(conexion.getSocket().getInputStream());

                mensaje = (Mensaje) in.readObject();

                System.out.println(mensaje);

                puertoDestino = mensaje.getPuertoDestino();
                puertoOrigen = mensaje.getPuertoOrigen();
                ipOrigen = mensaje.getIpOrigen();
                ipDestino = mensaje.getIpDestino();
                msg = mensaje.getMensaje();

                if( msg.equalsIgnoreCase("LLAMADA") && this.conexiones.contains(puertoDestino)){

                    ControladorInicioNuevo.get(false).error("No es posible conectar. Ocupado");

                }else{

                    if( msg.equalsIgnoreCase("LLAMADA ACEPTADA") ){
                        this.conexiones.add(puertoDestino);
                        this.conexiones.add(puertoOrigen);
                    }
                    if( msg.equalsIgnoreCase("DESCONECTAR") ){
                        this.conexiones.remove(puertoDestino);
                        this.conexiones.remove(puertoOrigen);
                    }

                    this.vistaServidor.muestraMensaje("ORIGEN: " + ipOrigen + " => DESTINO: " + ipDestino + " :\n" + msg + "\n\n");

                    //Creo socket: Reenvio del mensaje
                    this.conexion.crearConexionEnvio(ipDestino, puertoDestino);

                    ObjectOutputStream out = new ObjectOutputStream(this.conexion.getSocket().getOutputStream());

                    out.writeObject(mensaje);

                    this.conexion.cerrarConexion();
                }

                this.conexion.cerrarConexion();

            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
