package configuracion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.io.*;

public class Configuracion implements Configurar{
    //singleton
    private static Configuracion config = null;
    private String ip;
    private int puerto;

    private static final String path = "chat.config";

    private Configuracion() {
        leerArchivoConfiguracion();
    }
    public static Configuracion getConfig(){
        if (config==null)
            config = new Configuracion();
        return config;
    }

    @Override
    public void leerArchivoConfiguracion(Object... args) {
        try {
            File file = new File(path);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
                this.ip = "localhost";
                this.puerto = 1500;
                escribirArchivoConfiguracion(this.ip, String.valueOf(this.puerto));
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
    }

    public boolean puertoValido(int puerto){
        return (puerto>0 && puerto<65535);
    }

    public boolean ipValida (String ip){
        String[] groups = ip.split("\\.");
        if (groups.length != 4) {
            return false;
        }
        try {
            return Arrays.stream(groups)
                    .filter(s -> s.length() >= 0) // && s.startsWith("0"))
                    .map(Integer::parseInt)
                    .filter(i -> (i >= 0 && i <= 255))
                    .count() == 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    public void escribirArchivoConfiguracion(Object... args) throws Exception{

        String ipAux;

        this.ip = (String) args[0];
        this.puerto = (int) args[1];

        if (ip.equals("localhost")){
            InetAddress address = InetAddress.getLocalHost();
            ipAux = address.getHostAddress();
        }
        else
            ipAux = this.ip;
        try {
            File file = new File(path);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(ipAux);
            bw.newLine();
            bw.write(String.valueOf(this.puerto));
            bw.close();
        } catch (IOException e) {
            throw new Exception(e);
        }

    }

    @Override
    public boolean validarConfiguracion(Object... args) throws UnknownHostException{
        String ip = (String) args[0];
        int puerto = (int) args[1];
        if (ip.equals("localhost")){
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        }
        return ipValida(ip) && puertoValido(puerto);
    }


    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }


    @Override
    public String[] getParametros() {
        String[] param = new String[2];
        param[0] = this.getIp();
        param[1] = String.valueOf(this.getPuerto());
        return param;
    }

}