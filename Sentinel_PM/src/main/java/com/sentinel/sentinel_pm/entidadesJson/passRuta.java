package com.sentinel.sentinel_pm.entidadesJson;

public class passRuta {
    private String passwd;
    private  String ruta;

    //constructor
    public passRuta(String passwd, String ruta) {
        this.passwd = passwd;
        this.ruta = ruta;
    }

    //getters y setters
    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
