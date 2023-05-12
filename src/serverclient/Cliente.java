package serverclient;

import controlador.ControladorInicioNuevo;
import controlador.ControladorRecepcionLlamada;
import controlador.ControladorSesionLlamada;
import encriptacion.RSA;
import mensaje.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;

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
    private final RSA rsa;

    private PublicKey publicKeyExtremo;

    private Cliente(){
        this.rsa = new RSA();
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

            //Ante mensaje de "Aviso" no debo cifrarlos
            if( msg.equals("LLAMADA") || msg.equals("DESCONECTAR") ) {
                mensaje.setMensaje(msg);
                if (msg.equals("LLAMADA"))
                    mensaje.setPublicKey(this.rsa.getPublicKey()); //Cuando yo llamo, ya envio mi clave publica (puede aceptarme)
            }else{
                mensaje.setMensaje( this.rsa.encriptar(msg, this.publicKeyExtremo) ); //Encripto con la llave publica que me envio
            }

            ObjectOutputStream out = new ObjectOutputStream(sCliente.getOutputStream());
            out.writeObject(mensaje);

            out.close();

            sCliente.close();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
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

                mensaje.setPublicKey(this.rsa.getPublicKey()); //Acepte la llamada, le envio mi clave publica al extremo para comenzar a intercambiar mensajes
                mensaje.setPuertoDestino(puertoDestino);
                mensaje.setIpDestino(ipDestino);
                mensaje.setMensaje(msg);
                mensaje.setIpOrigen(this.ipOrigen);
                mensaje.setPuertoOrigen(this.puertoOrigen);
            }

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
                    this.publicKeyExtremo = msg.getPublicKey(); //Recibo clave publica del extremo (puedo aceptar la llamada)
                }else if( txt.equalsIgnoreCase("LLAMADA ACEPTADA") ){
                    this.publicKeyExtremo = msg.getPublicKey(); //Recibo la clave publica del extremo que acepto mi llamada
                    ControladorInicioNuevo.get(false);
                    ControladorSesionLlamada.get(true);
                }else if( txt.equalsIgnoreCase("DESCONECTAR") ){
                    ControladorSesionLlamada.get(false).esconderVista();
                    ControladorSesionLlamada.get(false).borrarHistorial();
                    ControladorInicioNuevo.get(true).limpiarCampos();
                    this.publicKeyExtremo = null;
                }else{
                    String mensajeDesencriptado = this.rsa.desencriptar(txt); //Lo desencripto con mi clave privada. El extremo encripto con mi clave publica (enviada)
                    ControladorSesionLlamada.get(false).muestraMensaje(ipD + ": " + mensajeDesencriptado);
                }

                soc.close();

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
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
