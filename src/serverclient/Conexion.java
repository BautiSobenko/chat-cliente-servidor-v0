package serverclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexion implements IConexion {

    ServerSocket serverSocket;
    Socket socket;
    int puertoOrigen;
    int puertoDestino;

    @Override
    public void establecerConexion(Object... args) {
        try {
            int puertoOrigen = (int) args[0];
            this.serverSocket = new ServerSocket(puertoOrigen);
            System.out.println("Escuchando desde puerto: " + this.puertoOrigen);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObjectInputStream getInputStreamConexion() {
        try {
            return new ObjectInputStream( this.socket.getInputStream() );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObjectOutputStream getOutputStreamConexion() {
        try {
            return new ObjectOutputStream( this.socket.getOutputStream() );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void aceptarConexion() {
        try {
            this.socket = this.serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cerrarConexion() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void crearConexionEnvio(Object... args) {
        String ipServer = (String) args[0];
        int puertoServidor = (int) args[1];
        try {
            this.socket = new Socket(ipServer,puertoServidor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
