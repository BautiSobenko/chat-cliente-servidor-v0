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

            ServerSocket servidor = new ServerSocket(this.puertoServer);

            this.vistaServidor.muestraMensaje("Servidor Iniciado! \nPuerto: " + this.puertoServer + "\n");

            String ipOrigen, ipDestino, msg;
            int puertoDestino;
            int puertoOrigen;
            Socket soc;
            Mensaje mensaje;

            while (true) {

                soc = servidor.accept();

                ObjectInputStream in = new ObjectInputStream(soc.getInputStream());

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

                    Socket enviaDestinatario = new Socket(ipDestino, puertoDestino);

                    ObjectOutputStream out = new ObjectOutputStream(enviaDestinatario.getOutputStream());

                    out.writeObject(mensaje);

                    enviaDestinatario.close();
                }

                soc.close();

            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
