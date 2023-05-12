package serverclient;

import controlador.ControladorInicioNuevo;
import controlador.ControladorRecepcionLlamada;
import controlador.ControladorSesionLlamada;
import mensaje.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente implements Runnable {

    private static Cliente cliente = null;

    private Socket sCliente;
    private ServerSocket ssCliente;
    private int puertoDestino;
    private int puertoOrigen;
    private int puertoServidor;
    private String ipServer;
    private String ipDestino;
    private String ipOrigen;

    private Cliente(){
    }

    public static Cliente getCliente() {
        if( cliente == null) {
            cliente = new Cliente();
        }
        return cliente;
    }


    public void enviaMensaje(String msg) {
        try {
            sCliente = new Socket(ipServer, this.puertoServidor);

            Mensaje mensaje = new Mensaje();

            InetAddress adress = InetAddress.getLocalHost(); //Obtengo la ip origen (Informacion extra)
            this.ipOrigen = adress.getHostAddress();

            mensaje.setPuertoOrigen(this.puertoOrigen);
            mensaje.setIpOrigen(this.ipOrigen);
            mensaje.setIpDestino(this.ipDestino);
            mensaje.setPuertoDestino(this.puertoDestino);
            mensaje.setMensaje(msg);

            ObjectOutputStream out = new ObjectOutputStream(sCliente.getOutputStream());
            out.writeObject(mensaje);

            out.close();

            sCliente.close();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void enviaMensaje(String msg, String ipDestino, int puertoDestino) {
        try {
            sCliente = new Socket(ipServer, this.puertoServidor);

            Mensaje mensaje = new Mensaje();

            InetAddress adress = InetAddress.getLocalHost(); //Obtengo la ip origen (Informacion extra)
            this.ipOrigen = adress.getHostAddress();

            if (msg.equalsIgnoreCase("LLAMADA ACEPTADA")) {

                this.puertoDestino = puertoDestino;
                this.ipDestino = ipDestino;

                mensaje.setPuertoDestino(puertoDestino);
                mensaje.setIpDestino(ipDestino);
                mensaje.setMensaje(msg);
                mensaje.setIpOrigen(this.ipOrigen);
                mensaje.setPuertoOrigen(this.puertoOrigen);
            }

            System.out.println(mensaje);

            ObjectOutputStream out = new ObjectOutputStream(sCliente.getOutputStream());
            out.writeObject(mensaje);

            out.close();

            sCliente.close();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {

            System.out.println(puertoOrigen);

            ssCliente = new ServerSocket(this.puertoOrigen);

            System.out.println("Escuchando desde puerto: " + this.puertoOrigen);

            String ipD, ipO, txt;
            Socket soc;
            Mensaje msg;
            while(true) {

                soc = ssCliente.accept();

                ObjectInputStream in = new ObjectInputStream(soc.getInputStream());

                msg = (Mensaje) in.readObject();

                txt = msg.getMensaje();
                ipO = msg.getIpOrigen();
                ipD = msg.getIpDestino();

                if( txt.equalsIgnoreCase("LLAMADA") ){
                    ControladorRecepcionLlamada controladorRecepcionLlamada = ControladorRecepcionLlamada.get(false);
                    controladorRecepcionLlamada.setIpOrigen(ipD);
                    controladorRecepcionLlamada.setIpDestino(ipO); //IP Del que me envio ese mensaje
                    controladorRecepcionLlamada.setPuertoDestino(msg.getPuertoOrigen()); //Puerto Del que me envio ese mensaje
                    controladorRecepcionLlamada.actualizarLabelIP(ipO);
                    ControladorRecepcionLlamada.get(true);
                }else if( txt.equalsIgnoreCase("LLAMADA ACEPTADA") ){
                    ControladorInicioNuevo.get(false);
                    ControladorSesionLlamada.get(true);
                }else if( txt.equalsIgnoreCase("DESCONECTAR") ){
                    ControladorSesionLlamada.get(false).esconderVista();
                    ControladorInicioNuevo.get(true);
                }else{
                    ControladorSesionLlamada.get(false).muestraMensaje(ipD + ": " + txt);

                }

                soc.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    public int getPuertoServidor() {
        return puertoServidor;
    }

    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }
}
