package serverclient;

import configuracion.ConfiguracionServer;
import controlador.ControladorInicioNuevo;
import controlador.ControladorRecepcionLlamada;
import controlador.ControladorSesionLlamada;
import encriptacion.Encriptacion;
import encriptacion.RSA;
import mensaje.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class Cliente implements Runnable,Emision,Recepcion {

    //singleton
    private static Cliente cliente = null;

    public final Conexion conexion;

    private int puertoServidor;
    private int puertoDestino;
    private int puertoOrigen;
    private String ipServer;
    private String ipDestino;
    private String ipOrigen;

    private PublicKey publicKeyExtremo;

    private final Encriptacion<PublicKey> rsa;

    private Cliente(){
        this.rsa = new RSA();
        this.conexion = new Conexion();
    }

    public static Cliente getCliente() {
        if( cliente == null) {
            cliente = new Cliente();
        }
        return cliente;
    }

    @Override
    public void enviaMensaje(String msg) {
        try {
            this.conexion.crearConexionEnvio(this.ipServer, this.puertoServidor);

            Mensaje mensaje = new Mensaje();

            InetAddress adress = InetAddress.getLocalHost(); //Obtengo la ip origen (Informacion extra)
            this.ipOrigen = adress.getHostAddress();
            mensaje.setPuertoOrigen(this.puertoOrigen);
            mensaje.setIpOrigen(this.ipOrigen);

            if( ipDestino.equals("localhost") ) //Si es "localhost", debo trabajo con la IP real, no con la String "localhost"
                mensaje.setIpDestino(adress.getHostAddress());
            else
                mensaje.setIpDestino(this.ipDestino);

            mensaje.setPuertoDestino(this.puertoDestino);

            //Los mensajes de "Control" no debo cifrarlos
            if( msg.equals("LLAMADA") || msg.equals("DESCONECTAR") || msg.equals("REGISTRO") ) {
                mensaje.setMensaje(msg);

                if (msg.equals("LLAMADA"))
                    mensaje.setPublicKey(this.rsa.getPublicKey()); //Cuando yo llamo, ya envio mi clave publica (puede aceptarme)

            }else{
                mensaje.setMensaje( this.rsa.encriptar(msg, this.publicKeyExtremo) ); //Encripto con la llave publica que me envio
            }

            ObjectOutputStream out = this.conexion.getOutputStreamConexion();//new ObjectOutputStream(sCliente.getOutputStream());
            out.writeObject(mensaje);

            out.close();

            conexion.cerrarConexion();

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
            this.conexion.crearConexionEnvio(ipServer, this.puertoServidor);

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

            ObjectOutputStream out = this.conexion.getOutputStreamConexion(); //new ObjectOutputStream(sCliente.getOutputStream());
            out.writeObject(mensaje);

            out.close();

            conexion.cerrarConexion();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            this.conexion.establecerConexion(this.puertoOrigen);

            String ipD, ipO, txt;
            Mensaje mensajeRecibido;

            while (true) {

                this.conexion.aceptarConexion();

                mensajeRecibido = this.recibeMensaje();

                txt = mensajeRecibido.getMensaje();
                ipO = mensajeRecibido.getIpOrigen();
                ipD = mensajeRecibido.getIpDestino();

                if (txt.equalsIgnoreCase("LLAMADA")) {
                    ControladorRecepcionLlamada controladorRecepcionLlamada = ControladorRecepcionLlamada.get(false);
                    controladorRecepcionLlamada.setIpOrigen(ipD);
                    controladorRecepcionLlamada.setIpDestino(ipO); //IP Del que me envio ese mensaje
                    controladorRecepcionLlamada.setPuertoDestino(mensajeRecibido.getPuertoOrigen()); //Puerto Del que me envio ese mensaje
                    controladorRecepcionLlamada.actualizarLabelIP(ipO);
                    ControladorRecepcionLlamada.get(true);
                    this.publicKeyExtremo = mensajeRecibido.getPublicKey(); //Recibo clave publica del extremo (puedo aceptar la llamada)
                } else if (txt.equalsIgnoreCase("LLAMADA ACEPTADA")) {
                    this.publicKeyExtremo = mensajeRecibido.getPublicKey(); //Recibo la clave publica del extremo que acepto mi llamada
                    ControladorInicioNuevo.get(false);
                    ControladorSesionLlamada.get(true);
                } else if (txt.equalsIgnoreCase("DESCONECTAR")) {
                    ControladorSesionLlamada.get(false).esconderVista();
                    ControladorSesionLlamada.get(false).borrarHistorial();
                    ControladorInicioNuevo.get(true).limpiarCampos();
                    this.publicKeyExtremo = null;
                } else {
                    String mensajeDesencriptado = this.rsa.desencriptar(txt); //Lo desencripto con mi clave privada. El extremo encripto con mi clave publica (enviada)
                    ControladorSesionLlamada.get(false).muestraMensaje(ipD + ": " + mensajeDesencriptado);
                }

                this.conexion.cerrarServer();

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public Conexion getConexion() {
        return conexion;
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
