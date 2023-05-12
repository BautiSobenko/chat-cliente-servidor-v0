package mensaje;

import java.io.Serializable;

public class Mensaje implements Serializable {

    String ipOrigen;
    String ipDestino;
    String usuario;
    String Mensaje;
    int puertoDestino;
    int puertoOrigen;
    
    public Mensaje(String ipDestino, String usuario, String mensaje, int puertoDestino, int puertoOrigen) {
        this.ipOrigen = ipOrigen;
        this.ipDestino = ipDestino;
        this.usuario = usuario;
        this.puertoDestino = puertoDestino;
        this.puertoOrigen = puertoOrigen;

    }

    public Mensaje() {
    }

    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(String ipDestino) {
        this.ipDestino = ipDestino;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "ipOrigen='" + ipOrigen + '\'' +
                ", ipDestino='" + ipDestino + '\'' +
                ", usuario='" + usuario + '\'' +
                ", Mensaje='" + Mensaje + '\'' +
                ", puertoDestino=" + puertoDestino +
                '}';
    }
}
