package configuracion;

import java.io.*;
import java.net.InetAddress;

public class ConfiguracionCliente extends Configuracion{
    //singleton
    private static ConfiguracionCliente config = null;
    private static final String path = "chat.config";

    private ConfiguracionCliente(String IP, int puerto) {
        super.setIp(IP);
        super.setPuerto(puerto);
        leerArchivoConfiguracion();
    }

    private ConfiguracionCliente() {
        leerArchivoConfiguracion();
    }

    public static ConfiguracionCliente getConfig(){
        if (config==null)
            config = new ConfiguracionCliente();
        return config;
    }

    public static ConfiguracionCliente getConfig(String IP, int puerto){
        if (config == null)
            config = new ConfiguracionCliente(IP, puerto);
        return config;
    }

    @Override
    public void escribirArchivoConfiguracion(Object... args) throws Exception{

        String ipAux;

        if (super.getIp().equals("localhost")){
            InetAddress address = InetAddress.getLocalHost();
            ipAux = address.getHostAddress();
        }
        else
            ipAux = super.getIp();
        try {
            File file = new File(path);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(ipAux);
            bw.newLine();
            bw.write(String.valueOf(super.getPuerto()));
            bw.close();
        } catch (IOException e) {
            throw new Exception(e);
        }

    }
    @Override
    public void leerArchivoConfiguracion(Object... args) {
        try {
            File file = new File(path);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
                super.setIp("localhost");
                this.setPuerto(1500);
                escribirArchivoConfiguracion(super.getIp(), String.valueOf(super.getPuerto()));
            }
            else {
                FileReader fw = new FileReader(file);
                BufferedReader bw = new BufferedReader(fw);
                super.setIp( bw.readLine());
                this.setPuerto(Integer.parseInt(bw.readLine()));
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
