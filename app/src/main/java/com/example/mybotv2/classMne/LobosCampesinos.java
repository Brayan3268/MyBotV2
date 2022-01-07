package com.example.mybotv2.classMne;

import android.widget.Toast;

import com.example.mybotv2.R;

import java.util.ArrayList;
import java.util.Random;

public class LobosCampesinos {

    private int nPlayers = 0;
    private ArrayList<String> namePlayers;
    private ArrayList<Integer> rolPlayers;
    private Integer picWolf = R.drawable.lobo;
    private Integer picFarmer = R.drawable.campesino;
    Random r = new Random();
    Random r2 = new Random();
    Random r3 = new Random();
    int rand2 = 0, rand3 = 0;

    public LobosCampesinos(){}

    public LobosCampesinos(int nPlayers, ArrayList<String> namePlayers, ArrayList<Integer> rolPlayers){
        this.nPlayers = nPlayers;
        this.namePlayers = namePlayers;
        this.rolPlayers = rolPlayers;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    public ArrayList<String> getNamePlayers() {
        return namePlayers;
    }

    /*public void setNamePlayers(ArrayList<String> namePlayers) {
        this.namePlayers = namePlayers;
    }*/

    public ArrayList<Integer> getRolPlayers() {
        return rolPlayers;
    }

    public void setRolPlayers(ArrayList<Integer> rolPlayers) {
        this.rolPlayers = rolPlayers;
    }

    public Integer putNameAndImage(int ins){
        return ins == 1 ? picWolf : picFarmer;
    }

    public ArrayList<Integer> asignarRoles(int lobos, int campesinos){
        int tempW = lobos, tempF = campesinos;

        ArrayList<Integer> toReturn = new ArrayList<>();

        r = new Random();

        while (tempW != 0 || tempF != 0){
            rand2 = r.nextInt(4) + 1;
            if(rand2 == 1 && tempW != 0){
                toReturn.add(1);
                tempW--;
            }

            if(rand2 != 1 && tempF != 0){
                toReturn.add(2);
                tempF--;
            }
        }
        return toReturn;
    }

    public ArrayList<String> correrInicioLista(ArrayList<String> listaEntrada){
        namePlayers = listaEntrada;
        r2 = new Random();
        rand2 = r2.nextInt(3) + 1;
        ArrayList<String> listaRetornar = new ArrayList<>();
        switch (rand2){
            case 1:
                listaRetornar = rotativo();
                break;
            case 2:
                intercambio();
                listaRetornar = namePlayers;
                break;
            case 3:
                intercambio();
                listaRetornar = rotativo();
                break;
        }
        return listaRetornar;
    }

    private ArrayList<String> rotativo(){
        ArrayList<String> nuevaLista = new ArrayList<>();
        for(int i = 1; i < namePlayers.size(); i++){
            nuevaLista.add(namePlayers.get(i));
        }
        nuevaLista.add(namePlayers.get(0));
        //Toast.makeText(getApplicationContext(), namePlayers.get(0), Toast.LENGTH_SHORT).show();
        return nuevaLista;
    }

    private void intercambio(){
        r3 = new Random();
        String temp = "";
        int tam = namePlayers.size() - 1;

        for(int i = 0; i < 3; i++){
            rand3 = r3.nextInt(tam);
            int seleccion = rand3, cambio = rand3;

            while(seleccion == cambio){
                cambio = r3.nextInt(namePlayers.size());
            }
            //Toast.makeText(getApplicationContext(), namePlayers.get(seleccion) + " - " + namePlayers.get(cambio), Toast.LENGTH_SHORT).show();

            temp = namePlayers.get(cambio);
            namePlayers.set(cambio, namePlayers.get(seleccion));
            namePlayers.set(seleccion, temp);
        }
    }

}
