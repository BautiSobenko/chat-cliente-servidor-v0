package main;

import configuracion.Configuracion;
import controlador.ControladorConfiguracion;
import controlador.ControladorInicioNuevo;
import controlador.ControladorRegistro;

public class Main{

    public static void main(String[] args) {

        ControladorConfiguracion.get(true);

        //! Registro al Server, la app debe iniciar con este controlador
        //ControladorRegistro.get(true);

    }
}
