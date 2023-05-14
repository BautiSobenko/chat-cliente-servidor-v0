package configuracion;

import serverclient.Cliente;

import java.io.*;

public class Configuracion {
    //singleton
    private static Configuracion config = null;
    private String ip;
    private int puerto;

    private static final String path = "chat.config";

    private Configuracion() {
        tomarIpPuertoDesdeArchivo();
    }
    public static Configuracion getConfig(){
        if (config==null)
            config = new Configuracion();
        return config;
    }
    public void tomarIpPuertoDesdeArchivo(){
        try {
            File file = new File(path);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            else {
                FileReader fw = new FileReader(file);
                BufferedReader bw = new BufferedReader(fw);
                this.ip = bw.readLine();
                this.puerto = Integer.parseInt(bw.readLine());
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ip = "0.0.0.0";
        this.puerto = 0;
    }
    public static boolean puertoValido(String port){
        int puerto = Integer.parseInt(port);
        return (puerto>0 && puerto<65535);
    }

    public void escribirIpPuertoArchivo(String ip, int puerto)throws Exception{
        File file = new File(path);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(ip));
            bw.write(String.valueOf(puerto));
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }
}