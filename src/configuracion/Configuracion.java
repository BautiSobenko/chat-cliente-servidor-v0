package configuracion;

import java.io.*;

public class Configuracion {

    private static final String path = "chat.config";

    public static int puertoDesdeArchivo(){
        int puerto =0;
        try {
            File file = new File(path);
            // Si el archivo no existe es creado
            if (!file.exists()) {
                file.createNewFile();
            }
            else {
                FileReader fw = new FileReader(file);
                BufferedReader bw = new BufferedReader(fw);
                puerto = Integer.parseInt(bw.readLine());
                bw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return puerto;
    }
    public static boolean puertoValido(){
        int puerto = puertoDesdeArchivo();
        return (puerto>0 && puerto<65535);
    }

    public static void escribirPuertoArchivo(int puerto)throws Exception{
        File file = new File(path);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(puerto));
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}