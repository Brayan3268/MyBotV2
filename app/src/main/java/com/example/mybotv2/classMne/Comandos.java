package com.example.mybotv2.classMne;

import java.util.HashMap;
import java.util.Map;

public class Comandos {

    private int numeroComando; //El identificador del comando
    private String[] comando; //Palabras claves para llamar el comando
    private String descripcion; //Descripción que el bot dice del comando
    private boolean enProgreso; //Es para saber si este comando se está ejecutando
    private String[] datosInsertarBaseDatos; //Los datos para insertarlos en la base de datos
    private boolean confirmacion; // La confirmación para saber si sí se inserta o no en la bd
    /*La ruta en la posición 0 es la de ingresar datos en la base de datos
    * en la posición 1 es la de leer datos en la base de datos
    * en la posición 2 es la de actualizar datos en la base de datos
    * en la posición 3 es la de eliminar datos en la base de datos*/
    private String[] rutas;
    private String[] datosLeerBaseDatos; //Los datos para leer una base de datos y decirla

    /**
     * Constructor vacio para el manejo temprano de los objetos
     */
    public Comandos() { }

    /**
     * Sobrecarga del constructor de la clase para el almacenamiento de la información de los
     * comandos, unicamente para los que no necesitan acceso a una base de datos
     * @param numeroComando El identificador del comando diferente a los demás
     * @param comando Las palabras claves para llamar al comando
     * @param descripcion La descripción del comando
     */
    public Comandos(int numeroComando, String[] comando, String descripcion) {
        this.numeroComando = numeroComando;
        this.comando = comando;
        this.descripcion = descripcion;
    }

    /**
     * Sobrecarga del constructor de la clase para el almacenamiento de la información de los
     * comandos, principalmente para los que necesitan acceso a una base de datos
     * @param numeroComando El identificador del comando diferente a los demás
     * @param comando Las palabras claves para llamar al comando
     * @param descripcion La descripción del comando
     * @param enProgreso Valor para saber si el comando está almacenando información actualmente
     * @param datosInsertarBaseDatos Los datos que se van a insertar en la base de datos
     * @param confirmacion La confirmación por voz para saber si sí se van a insertar los datos o no
     * @param rutas Las rutas del backend para acceder a cada base de datos
     * @param datosLeerBaseDatos Los datos que se leyeron de la base de datos
     */
    public Comandos(int numeroComando, String[] comando, String descripcion, boolean enProgreso,
                    String[] datosInsertarBaseDatos, boolean confirmacion, String[] rutas,
                    String[] datosLeerBaseDatos) {
        this.numeroComando = numeroComando;
        this.comando = comando;
        this.descripcion = descripcion;
        this.enProgreso = enProgreso;
        this.datosInsertarBaseDatos = datosInsertarBaseDatos;
        this.confirmacion = confirmacion;
        this.rutas = rutas;
        this.datosLeerBaseDatos = datosLeerBaseDatos;
    }

    /**
     * Metodo para obtener el numero del comando
     * @return numero del comando (int)
     */
    public int getNumeroComando() {
        return numeroComando;
    }

    public void setNumeroComando(int numeroComando) { this.numeroComando = numeroComando; }

    /**
     * Metodo para obtener las palabras claves del comando
     * @return lista de Strings con las palabras claves
     */
    public String[] getComando() {
        return comando;
    }

    public void setComando(String[] comando) {
        this.comando = comando;
    }

    /**
     * Metodo para obtener la descripción del comando
     * @return String con la descripción del comando
     */
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Metodo para saber si el comando actual está en proceso
     * @return booleano con el valor de si está en proceso o no
     */
    public boolean getEnProgreso() {
        return enProgreso;
    }

    /**
     * Metodo para asignar un estado a un proceso de activo o no activo
     * @param enProgreso booleano con el valor true si esta activo y false si no lo esta
     */
    public void setEnProgreso(boolean enProgreso) {
        this.enProgreso = enProgreso;
    }

    /**
     * Metodo para obtener la información a almacenar en la base de datos
     * @return Una lista con la información a insertar en la base de datos
     */
    public String[] getDatosInsertarBaseDatos() { return datosInsertarBaseDatos; }

    /**
     * Metodo para asignar la información a almacenar en la base de datos
     * @param datosInsertarBaseDatos Una lista con la información a insertar en la base de datos
     */
    public void setDatosInsertarBaseDatos(String[] datosInsertarBaseDatos) { this.datosInsertarBaseDatos = datosInsertarBaseDatos; }

    /**
     * Metodo para obtener el mensaje de si se tiene la confirmacion o no para almacenar en la bd
     * @return El mensaje de confirmación (String)
     */
    public boolean getConfirmacion() {
        return confirmacion;
    }

    /**
     * Metodo para asignar el mensaje de si se confirma o no para almacenar en la bd
     * @param confirmacion El mensaje de confirmación (String)
     */
    public void setConfirmacion(boolean confirmacion) {
        this.confirmacion = confirmacion;
    }

    /**
     * Metodo para obtener las rutas del backend
     * @return Una lista de Strings con las rutas
     */
    public String[] getRutas() { return rutas; }

    public void setRutas(String[] rutas) { this.rutas = rutas; }

    /**
     * Metodo para obtener los datos a almacenar en la base de datos
     * @return Una lista con los datos a almacenar en la base de datos
     */
    public String[] getDatosLeerBaseDatos() { return datosLeerBaseDatos; }

    public void setDatosLeerBaseDatos(String[] datosLeerBaseDatos) { this.datosLeerBaseDatos = datosLeerBaseDatos; }

}