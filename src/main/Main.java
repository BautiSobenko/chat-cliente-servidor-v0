package main;

import configuracion.Configuracion;
import controlador.ControladorConfiguracion;

public class Main{

    public static void main(String[] args) {
        Configuracion configuracion = Configuracion.getConfig();
        ControladorConfiguracion.get(true);

    }
}
