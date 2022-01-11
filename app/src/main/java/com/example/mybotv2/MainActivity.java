package com.example.mybotv2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.mybotv2.classMne.Comandos;
import com.example.mybotv2.classMne.Constants;
import com.example.mybotv2.classMne.TTSManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;

public class MainActivity extends AppCompatActivity {

    TextView grabar, respuesta;
    ImageButton img_btn_hablar;

    int numeroComandos, contador = 0;
    String strSpeech2Text = "", textoRepetido = "";
    final String[] diasSemana = new String[9];
    boolean isGreeting, isGoodbyes, esperarConfirmacion, puedeAgradecer, leyendo = false;
    boolean algunaBaseDatosProgreso = false;

    ArrayList<String> saludos = new ArrayList<>();
    ArrayList<String> saludosBot = new ArrayList<>();
    ArrayList<String> despedidas = new ArrayList<>();
    ArrayList<String> agradecimientos = new ArrayList<>();
    ArrayList<String> agradecimientosBot = new ArrayList<>();
    ArrayList<Comandos> listaComandos = new ArrayList<>();

    Random rand = new Random();

    TTSManager ttsManager = null;

    /* Los mensajes para que el bot sepa que es un saludo y que es una despedida y que responder */ {
        saludos.add("hola");
        saludos.add("buenos días");
        saludos.add("buenas tardes");
        saludos.add("buenas noches");
        saludos.add("buenas");
        saludos.add("buenos");

        saludosBot.add("Hola. ¿Que quieres hacer hoy?");
        saludosBot.add("Espero que estés teniendo un lindo día. ¿Que te gustaria hacer?");
        saludosBot.add("¡Que genial tenerte aquí!. ¿Que hacemos ahora?");
        saludosBot.add("Hola. ¡¿Con qué empezamos?!");

        despedidas.add("hasta luego");
        despedidas.add("chao");
        //despedidas.add("la buena");
        despedidas.add("todo bien");
        despedidas.add("adiós");
        despedidas.add("chau");
        despedidas.add("nos vemos");
        despedidas.add("nos pi");
        despedidas.add("hasta pronto");

        diasSemana[0] = "lunes";
        diasSemana[1] = "martes";
        diasSemana[2] = "miércoles";
        diasSemana[3] = "miercoles";
        diasSemana[4] = "jueves";
        diasSemana[5] = "viernes";
        diasSemana[6] = "sábado";
        diasSemana[7] = "sabado";
        diasSemana[8] = "domingo";

        agradecimientos.add("muchas gracias");
        agradecimientos.add("gracias");
        agradecimientos.add("mi dios le pague");
        agradecimientos.add("la buena");

        agradecimientosBot.add("¡Con mucho gusto!");
        agradecimientosBot.add("Para servirte");
        agradecimientosBot.add("me alegra haberte ayudado");
        agradecimientosBot.add("fue un placer");
        agradecimientosBot.add("espero seguir siendo de ayuda");
        agradecimientosBot.add("no hay porqué");
        agradecimientosBot.add("seguiré aquí por si me necesitas");
        agradecimientosBot.add("¡fue un trabajo en equipo!");
    }

    /* La lista de los comandos que reconoce la aplicación */ {
        listaComandos.add(new Comandos(1,
                new String[]{"fecha", "día"},
                "Si usas la palabra fecha ó día, te diré la fecha del día de hoy"));

        listaComandos.add(new Comandos(2,
                new String[]{"hora"},
                "Si usas la palabra hora, te diré la hora actual"));

        listaComandos.add(new Comandos(3, new String[]{"persona", "personas"},
                "Si usas la palabra personas, podrás agregar a alguien a la base de datos " +
                        "de las personas",
                false,
                new String[]{"Nombre", "", "Razón", "", "¿Ayudar?", ""},
                false,
                new String[]{Constants.URL_PEOPLE_FOR_KNOW_HELP_INSERT, Constants.URL_PEOPLE_FOR_KNOW_HELP_SELECT},
                new String[]{"nameperson", "", "reason", "", "help", ""}));

        listaComandos.add(new Comandos(4,
                new String[]{"idea", "ideas"},
                "Si usas la palabra ideas, podrás agregar una nueva idea a la base de datos " +
                        "de las ideas de programación",
                false,
                new String[]{"Idea", "", "colaboradores", ""},
                false,
                new String[]{Constants.URL_IDEAS_FOR_APPS_INSERT, Constants.URL_IDEAS_FOR_APPS_SELECT},
                new String[]{"ideadescription", "", "collaborator", ""}));

        listaComandos.add(new Comandos(5,
                new String[]{"juego", "juegos", "rescatado", "terminado", "finalizado"},
                "Si usas la palabra juegos, rescatado ó terminado podras guardar en la " +
                        "base de datos de los juegos rescatados un nuevo juego que hayas completado",
                false,
                new String[]{"Nombre", ""},
                false,
                new String[]{Constants.URL_GAME_FINISHED_INSERT, Constants.URL_GAME_FINISHED_READ},
                new String[]{"namegame", ""}));

        listaComandos.add(new Comandos(6,
                new String[]{"película", "películas", "serie", "series", "pelicula", "peliculas"},
                "Si usas las palabras película ó serie, podrás agregar una película o una serie " +
                        "a la base de datos",
                false,
                new String[]{"Nombre", "", "Tipo", ""},
                false,
                new String[]{Constants.URL_MOVIES_OR_SERIES, Constants.URL_MOVIES_OR_SERIES_SELECT},
                new String[]{"namemovieorserie", "", "typems", ""}));
        //libros, proyectos actuales, cosas por hacer
        //Guardar actividades para realizar con fecha, tipo calendario, dato random

        listaComandos.add(new Comandos(7,
                new String[]{"puedes", "sabes", "comando", "comandos"},
                "Si usas algunas de las palabras puedes, sabes ó comandos, te diré los " +
                        "comandos sobre todo lo que puedo hacer actualmente"));

        listaComandos.add(new Comandos(8,
                new String[]{"leer", "trae", "consulta", "lee", "léeme", "leeme"},
                "Si usas alguna de las palabras, leer, trae ó consulta, te dire la " +
                        "información que haya en la base de datos de tu elección"));

        listaComandos.add(new Comandos(9,
                new String[]{"tareas", "cosas", "tengo", "hay", "tarea"},
                "Si usas algunas de las palabras tareas, cosas, tengo, ó hay, te diré " +
                        "todas las cosas que tengas",
                false,
                new String[]{"Título", "", "Descripción", "", "¿Cuántos días tienes?", "", "Estado", ""},
                false,
                new String[]{Constants.URL_THINGS_TO_DO_INSERT, Constants.URL_THINGS_TO_DO_SELECT},
                new String[]{"title", "", "description", "", "datatodo", "", "status", ""}));

        listaComandos.add(new Comandos(10,
                new String[]{"agregar", "sugerir", "inexistente"},
                "Si usas alguna de las palabras agregar, sugerir ó inexistente podrás " +
                        "añadir un comando que te gustaría que pudiera hacer",
                false,
                new String[]{"¿Como quieres que se llame el comando?", "", "Dame una descripción para este comando", "", "¿Cual es tú nombre?", ""},
                false,
                new String[]{Constants.URL_COMAND_NON_EXIST_INSERT, Constants.URL_COMAND_NON_EXIST_SELECT},
                new String[]{"intentcomand", "", "description", "", "whosuggestion", "", "datasuggestion", ""}));

        numeroComandos = listaComandos.size();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());

        //Se inicializan los componentes
        grabar = findViewById(R.id.txtGrabarVoz);
        respuesta = findViewById(R.id.txtGrabarRes);
        img_btn_hablar = findViewById(R.id.img_btn_hablar);
        ttsManager = new TTSManager();
        ttsManager.init(getApplicationContext());

        //Cuando se presiona el imagebutton este comienza a escuchar
        img_btn_hablar.setOnClickListener(v -> iniciarEntradaVoz());
    }

    /**
     * Con este metodo se crea el objeto para dar inicio al reconocimiento por voz, con un intent,
     * se comienza intentando dar inicio y se valida si el dispositivo si tiene soporte al
     * reconocimiento por voz
     */
    public void iniciarEntradaVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bienvenido");

        try {
            // This starts the activity and populates the intent with the speech text.
            speechResultLauncher.launch(intent);
        } catch (ActivityNotFoundException ignored) {
            Toast.makeText(getApplicationContext(),
                    "Tú dispositivo no soporta el reconocimiento por voz",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Este es el resultado al llamado del intent, trayendo así en una variable el resultado de lo
     * reconocido por voz
     */
    private final ActivityResultLauncher<Intent> speechResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    strSpeech2Text = speech.get(0);
                    grabar.setText(strSpeech2Text);

                    //Es para agradecer al bot
                    if(agradecer(strSpeech2Text.toLowerCase())){
                        return;
                    }

                    //Se valida si el texto es un saludo o una despedida
                    if (!saludoDespedida(strSpeech2Text.toLowerCase())) {
                        return;
                    }

                    //Cancela el proceso actual
                    if (cancelarProceso(strSpeech2Text.toLowerCase())) {
                        return;
                    }

                    //Proceso para leer una base de datos
                    if (leerBaseDatos(strSpeech2Text)) {
                        return;
                    }

                    //Confirma los datos para subirlos a la base de datos
                    if (confirmarDatos(strSpeech2Text)) {
                        return;
                    }

                    /*Agrega los datos necesarios a cada comando si este fue llamado y
                    hasta terminar de llenar los datos*/
                    if(agregarDatosNecesarios(strSpeech2Text)){
                        return;
                    }

                    /*Si ya saludó y no hay ninguna bd en progreso entonces se mira que
                    comando ejecutar*/
                    if(buscarComandoEjecutar(strSpeech2Text)){
                        return;
                    }

                    //Se valida si el texto es un saludo o una despedida
                    Toast.makeText(this, "aaaaaaaa", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "aaaaaaaa", Toast.LENGTH_SHORT).show();

                }

            }
    );

    /**
     * Metodo para cuando se le dan las gracias al bot y se devuelve una respuesta al azar
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean agradecer(String texto){
        for (String agradecimiento : agradecimientos) {
            //Se mira si la palabra está en la lista de los agradecimientos o no
            if (texto.contains(agradecimiento) && puedeAgradecer) {
                /*Se elige una contestación al azar y se devuelve al usuario por voz y la variable
                agradecimiento se pone en false para dejar claro que ya no hay nada porqué agradecer*/
                int random = rand.nextInt(agradecimientosBot.size());

                String texto2Return = agradecimientosBot.get(random);
                respuesta.setText(texto2Return);
                ttsManager.initQueue(texto2Return);
                puedeAgradecer = false;
                return true;
            }

            //Por si no hay nada que agradecer
            if(texto.contains(agradecimiento) && !puedeAgradecer){
                String res = "¡No hay nada que agradecer!";
                respuesta.setText(res);
                ttsManager.initQueue(res);
                return true;
            }
        }
        return false;
    }

    /**
     * Con este metodo se validan los saludos y se envía un mensaje contestando dicho saludo
     *
     * @param texto El saludo que se reconocio por voz
     */
    public boolean saludoDespedida(String texto) {
        //Se recorre la lista de los saludos
        for (String saludo : saludos) {
            //Se mira si la palabra está en la lista de los saludos o no
            if (texto.contains(saludo)) {
                /*Se elige una contestación al azar y se devuelve al usuario por voz y la variable
                saludo se pone en true para poder continuar con la ejecución, ya que si no saluda
                no puede hacer uso de las demás funciones */
                int random = rand.nextInt(saludosBot.size());

                String texto2Return = saludosBot.get(random);
                respuesta.setText(texto2Return);
                ttsManager.initQueue(texto2Return);
                isGreeting = true;
                return false;
            }
        }

        //Se recorre la lista de las despedidas
        for (String despedida : despedidas) {
            //Se mira si la palabra está en la lista de las despedidas o no
            if (despedida.equalsIgnoreCase(texto)) {
                //Se responde por voz y mediante una tostada y se cierra la ejecución de la app
                Toast.makeText(getApplicationContext(), "Cuídate", Toast.LENGTH_LONG).show();
                ttsManager.initQueue("Cuídate");
                isGoodbyes = true;
                new Hilo1().start();
                return false;
            }
        }

        //Se mira si se saludo o se despedio para dar un mensaje al usuario insitandolo a saludar
        if (!isGreeting && !isGoodbyes)
            ttsManager.initQueue("Ten un poco de modales. por favor, salúdame");
        return true;
    }

    /**
     * Metodo para cancelar el proceso actual
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean cancelarProceso(String texto) {
        if (leyendo || algunaBaseDatosProgreso) {
            if (texto.contains("can") || strSpeech2Text.toLowerCase().contains("olv")) {
                resetearDatos("Ok. Cancelado");
                puedeAgradecer = true;
                return true;
            }
            return false;
        } else {
            if (texto.contains("can") || strSpeech2Text.toLowerCase().contains("olv")) {
                ttsManager.addQueue("No hay procesos para cancelar");
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo para leer una base de datos
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean leerBaseDatos(String texto) {
        if (leyendo) {
            comandoLeer(texto);
            return true;
        }
        return false;
    }

    /**
     * Metodo para confirmar los datos que se van a subir a la base de datos, sino, se realizan
     * los procedimientos de resetear la base de datos y/o ver si se puede cancelar el proceso
     * actual
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean confirmarDatos(String texto) {
        if (esperarConfirmacion) {
            if (texto.toLowerCase().contains("s") || texto.toLowerCase().contains("co")) {
                try {
                    Comandos c = mirarBaseDatosEnProgreso();
                    c.setConfirmacion(true);
                    pedirConfirmacion(c);
                    c.setConfirmacion(false);
                    puedeAgradecer = true;
                } catch (InterruptedException ignored) {}
            } else {
                resetearDatos("Ok. Cancelado");
            }
            return true;
        } else {
            cancelarProceso(texto);
        }
        return false;
    }

    /**
     * Metodo para agregar los datos necesarios a un comando para ser usado luego de la confirmación
     * del usuario
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean agregarDatosNecesarios(String texto){
        if (algunaBaseDatosProgreso) {
            agregarDatosComando(texto);
            puedeAgradecer = true;
            return true;
        }
        return false;
    }

    /**
     * Metodo para buscar el comando que el usuario solicitó
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean buscarComandoEjecutar(String texto){
        if (isGreeting) { //if (isGreeting && !algunaBaseDatosProgreso) { ASI ESTABA ESTA LINEA POR SI ALGO SALE MAL
            comandosParaEjecutar(texto);
            return true;
        }
        return false;
    }

    /**
     * Metodo que resetea los datos que se tenian almacenados y se estaban recopilando
     * (independientemente de la cantidad de datos obtenida hasta el momento) y los elimina y
     * reestablece las demás variables como 'leyendo', 'esperarConfirmacion', etc
     * @param texto Mensaje del usuario
     */
    public void resetearDatos(String texto) {
        ttsManager.addQueue(texto);
        algunaBaseDatosProgreso = false;
        Comandos c = mirarBaseDatosEnProgreso();
        if (c != null) {
            c.setEnProgreso(false);
            c.setConfirmacion(false);
            String[] nuevosDatos = c.getDatosInsertarBaseDatos();
            for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
                if (i % 2 != 0) {
                    nuevosDatos[i] = "";
                }
            }
            c.setDatosInsertarBaseDatos(nuevosDatos);
        }

        esperarConfirmacion = false;
        /*assert c != null;
        c.setConfirmacion(false);*/
        leyendo = false;
        puedeAgradecer = true;
    }


    public void agregarDatosComando(String texto) {
        Comandos c = mirarBaseDatosEnProgreso();
        boolean ciclo = true;
        int i = 0;
        try {
            String[] nuevosDatos = c.getDatosInsertarBaseDatos();
            while (ciclo && i < nuevosDatos.length) {
                if (i % 2 != 0 && nuevosDatos[i].equals("")) {
                    nuevosDatos[i] = texto;
                    c.setDatosInsertarBaseDatos(nuevosDatos);
                    ciclo = false;
                    if (i < nuevosDatos.length - 1) {
                        ttsManager.addQueue(c.getDatosInsertarBaseDatos()[i + 1]);
                        try {
                            while (ttsManager.getIsSpeaking()) { //noinspection BusyWait
                                Thread.sleep(2000);
                            }
                        } catch (Exception ignored) {}
                        iniciarEntradaVoz();
                    }
                    if (i == nuevosDatos.length - 1) {
                        ciclo = true;
                    }
                }
                i++;
            }
            if (ciclo) {
                comandosParaEjecutar(c.getComando()[0]);
            }
        } catch (Exception ignored) {
        }
    }


    public Comandos obtenerComandoPeticion(String texto) {
        for (Comandos c : listaComandos) {
            for (String s : c.getComando()) {
                if (texto.toLowerCase().contains(s.toLowerCase())) {
                    return (c);
                }
            }
        }
        return new Comandos();
    }

    /**
     * Con el texto que ingresa por parametro se comienza a recorrer la lista de los comandos y a
     * su vez las palabras claves de cada comando, buscando coincidencias con el texto para poder
     * determinar cual es el comando solicitado y se pasa el código del comando al método que los
     * ejecuta
     *
     * @param texto Lo que se reconocio por voz del usuario
     */
    public void comandosParaEjecutar(String texto) {
        for (Comandos c : listaComandos) {
            for (String s : c.getComando()) {
                if (texto.toLowerCase().contains(s.toLowerCase())) {
                    llamadoComando(c.getNumeroComando(), c, texto);
                    return;
                }
            }
        }
        llamadoComando(0, obtenerComandoPeticion("sugerir"), texto);
    }

    /**
     * Se ejecuta un comando con el código del comando, algunos necesitan además su propio comando
     * para usar los datos base de ellos (como los campos a llenar para la base de datos) y el
     * texto por si alguno debe ejecutar algo mediante la elección de palabras del usuario (como
     * leer una base de datos determinada)
     *
     * @param comandoParaEjecutar El código del comando seleccionado
     * @param comando             El objeto del comando seleccionado
     * @param texto               El texto reconocido por voz
     */
    public void llamadoComando(int comandoParaEjecutar, Comandos comando, String texto) {
        switch (comandoParaEjecutar) {
            case 1:
                comandoFecha();
                break;
            case 2:
                comandoHora();
                break;
            case 3:
                comandoPersonas(comando);
                break;
            case 4:
                comandoIdeas(comando);
                break;
            case 5:
                comandoJuegos(comando);
                break;
            case 6:
                comandoPeliculasSeries(comando);
                break;
            case 7:
                comandoComandos();
                break;
            case 8:
                comandoLeer(texto);
                break;
            case 9:
                comandoCosasToDo(comando, texto);
                break;
            case 10:
                comandoSugerirComandos(comando, texto);
                break;

                /*Si no coincide con ninguno de los comandos y no es una despedida entonces pide
                repetirlo otra vez y si vuelve a decir lo mismo entonces da un mensaje diferente
                posteriormente lo guarda en la base de datos de las ideas de los usuarios y si
                luego dice algo diferente que tampoco este en los comandos vuelve a empezar el
                ciclo pidiendo que lo repitan otra vez*/
            default:

                if (!strSpeech2Text.equalsIgnoreCase(textoRepetido) && contador > 0) {
                    contador = 0;
                }
                if (!isGoodbyes && contador == 0) {
                    ttsManager.addQueue("Lo siento. ¿Podrías repetirlo otra vez?");
                    contador++;
                    textoRepetido = texto;
                } else {
                    String m = "Aún no se hacer eso, pero lo guardaré en la base de datos para que " +
                            "sea posible pronto";
                    ttsManager.addQueue(m);

                    String[] nuevosDatos = comando.getDatosInsertarBaseDatos();
                    nuevosDatos[0] = "El nombre que intentaste fue:";
                    nuevosDatos[1] = texto;
                    comando.setDatosInsertarBaseDatos(nuevosDatos);
                    comandoSugerirComandos(comando, texto);
                }
                break;
        }
    }

    /**
     * Mira si hay una base de datos que tenga su atributo 'enProgreso' en true para continuar
     * llenando los datos que se requieren para finalizar exitosamente el comando
     *
     * @return Un comando
     */
    public Comandos mirarBaseDatosEnProgreso() {
        for (Comandos c : listaComandos) {
            if (c.getEnProgreso())
                return c;
        }
        return null;
    }

    public void solicitarDatosNecesarios(Comandos c) {
        for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
            if (i % 2 != 0 && c.getDatosInsertarBaseDatos()[i].equals("")) {
                algunaBaseDatosProgreso = true;
                c.setEnProgreso(true);
                ttsManager.addQueue(c.getDatosInsertarBaseDatos()[i - 1]);
                respuesta.setText(c.getDatosInsertarBaseDatos()[i - 1]);
                try {
                    while (ttsManager.getIsSpeaking()) { //noinspection BusyWait
                        Thread.sleep(2000);
                    }
                } catch (Exception ignored) {
                }
                iniciarEntradaVoz();
                i = c.getDatosInsertarBaseDatos().length;
            }
        }
    }


    /**
     * Este metodo sirve para decirle al usuario la fecha actual con día, mes y año
     */
    public void comandoFecha() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("EEEE/dd/MM/yyyy");

        String fecha = "El día de hoy es " + df.format(c.getTime());
        ttsManager.initQueue(fecha);
        respuesta.setText(fecha);
        puedeAgradecer = true;
    }

    /**
     * Este metodo sirve para decirle al usuario la hora actual
     */
    public void comandoHora() {
        long ahora = System.currentTimeMillis();
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(ahora);
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);
        String horaCompleta = "";
        if (hora <= 12) {
            horaCompleta = "Son las " + hora + " y " + minuto + " de la mañana";
        } else {
            if (hora <= 18) {
                horaCompleta = "Son las " + (hora - 12) + " y " + minuto + " de la tarde";
            } else {
                if (hora > 19) {
                    horaCompleta = "Son las " + (hora - 12) + " y " + minuto + " de la noche";
                }
            }
        }
        ttsManager.initQueue(horaCompleta);
        respuesta.setText(horaCompleta);
        puedeAgradecer = true;
    }

    private void comandoPersonas(Comandos c) {
        solicitarDatosNecesarios(c);
        if ((c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0) &&
                c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("namePerson", c.getDatosInsertarBaseDatos()[1]);
            datos.put("reason", c.getDatosInsertarBaseDatos()[3]);
            datos.put("help", c.getDatosInsertarBaseDatos()[5]);
            datos.put("dataPeopleForKnowHelp", date());
            recordGeneral(datos, c);
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
        }
    }

    public void comandoIdeas(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("ideaDescription", c.getDatosInsertarBaseDatos()[1]);
            datos.put("dataIdea", date());
            datos.put("collaborator", c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
        }
    }

    public void comandoJuegos(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("nameGame", c.getDatosInsertarBaseDatos()[1]);
            recordGeneral(datos, c);
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
        }
    }

    public void comandoPeliculasSeries(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("nameMovieOrSerie", c.getDatosInsertarBaseDatos()[1]);
            datos.put("typeMS", c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
        }
    }

    public void comandoSugerirComandos(Comandos c, String texto) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("intentComand", c.getDatosInsertarBaseDatos()[1]);
            datos.put("description", c.getDatosInsertarBaseDatos()[3]);
            datos.put("whoSuggestion", c.getDatosInsertarBaseDatos()[5]);
            datos.put("dataSuggestion", date());
            recordGeneral(datos, c);
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
        }
    }

    public void comandoComandos() {
        StringBuilder comandos = new StringBuilder();
        for (Comandos c : listaComandos) {
            comandos.append(c.getNumeroComando()).append(")");
            for (String s : c.getComando()) {
                if (c.getNumeroComando() == numeroComandos && s.equalsIgnoreCase(c.getComando()[c.getComando().length - 1])) {
                    comandos.append(s).append(".");
                } else {
                    if ((c.getComando().length > 1) && !s.equalsIgnoreCase(c.getComando()[c.getComando().length - 1])) {
                        comandos.append(s).append(" - ");
                    } else {
                        comandos.append(s).append(".\n\n");
                    }
                }
            }
            if (ttsManager.getIsLoaded()) {
                ttsManager.addQueue(c.getDescripcion());
            } else {
                ttsManager.initQueue(c.getDescripcion());
            }
        }
        respuesta.setText(comandos.toString());
        ttsManager.addQueue("Y bien. ¿Que quieres hacer primero?");
        puedeAgradecer = true;
    }

    public void comandoLeer(String texto) {
        if (leyendo) {
            for (Comandos c : listaComandos) {
                for (String s : c.getComando()) {
                    if (texto.toLowerCase().contains(s.toLowerCase())) {
                        c.setEnProgreso(true);
                        readDataBase(c);
                        break;
                    }
                }
            }
        } else {
            leyendo = true;
            ttsManager.addQueue("¿Que base de datos quieres consultar?");
            try {
                while (ttsManager.getIsSpeaking()) { //noinspection BusyWait
                    Thread.sleep(500);
                }
            } catch (Exception ignored) {
            }
            iniciarEntradaVoz();
        }
    }

    public void comandoCosasToDo(Comandos c, String texto) {
        //String dia = tomarDiaSeleccionado(texto);
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("title", c.getDatosInsertarBaseDatos()[1]);
            datos.put("description", c.getDatosInsertarBaseDatos()[3]);
            datos.put("datatoinsert", date());
            datos.put("dataToDo", c.getDatosInsertarBaseDatos()[5]);
            datos.put("status", c.getDatosInsertarBaseDatos()[7]);
            recordGeneral(datos, c);
        } else {
            Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
        }
        //ttsManager.addQueue(dia);
    }

    /**
     * @param texto Texto ingresado por el ususario que contiene el día seleccionado para la consulta
     * @return ds = día de la semana
     */
    public String tomarDiaSeleccionado(String texto) {
        String dia = (texto);
        for (String ds : diasSemana) {
            if (dia.toLowerCase().contains(ds)) {
                return ds;
            }
        }
        return "";
    }

    /**
     * Method where the user validate and register in the postgresql
     *
     * @param datos asdf
     */
    private void recordGeneral(Map<String, String> datos, Comandos c) {
        if (!esperarConfirmacion) {
            try {
                pedirConfirmacion(c);
            } catch (InterruptedException ignored) {
            }
        }
        if (algunaBaseDatosProgreso && esperarConfirmacion && c.getConfirmacion()) {
            //Is mapped the data and convert in Json object
            JSONObject jsonData = new JSONObject(datos);

            //Register a new user in the app
        /*Is set medium priority for de query is quickly (if is set in low priority) and don't
                                consume much the battery of phone (if is set in high priority)*/
            AndroidNetworking.post(c.getRutas()[0])
                    .addJSONObjectBody(jsonData)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    //When the query is executed without errors
                    try {
                        //Case happy
                        String respuesta = response.getString("respuesta");
                        String error = response.getString("error");
                        if (respuesta.equals("-1")) {
                            ttsManager.addQueue(error);
                        }
                        if (respuesta.equals("200")) {
                            Toast.makeText(getApplicationContext(), "Registro insertado con exito", Toast.LENGTH_LONG).show();
                            resetearDatos("Listo, ya fue agregado a la base de datos");
                            puedeAgradecer = true;
                        }
                    } catch (JSONException e) {
                        //Case sad with the query
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        ttsManager.addQueue("Persona no insertada");
                        resetearDatos("");
                    }
                }

                @Override
                public void onError(ANError anError) {
                    //Is execute when the query has happened an error
                    Toast.makeText(getApplicationContext(), "Error: " + anError.getErrorDetail(), Toast.LENGTH_LONG).show();
                    ttsManager.addQueue("Persona no insertada");
                    resetearDatos("");
                }
            });
        }
    }

    private void readDataBase(Comandos c) {
        AndroidNetworking.post(c.getRutas()[1])
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String res = response.getString("respuesta");
                            if (res.equals("200")) {
                                JSONArray array = response.getJSONArray("data");
                                ttsManager.addQueue("Los datos son:");
                                StringBuilder mostrar = new StringBuilder();

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jlo = array.getJSONObject(i);

                                    //JSONObject objectUser = response.getJSONObject("data");
                                    String[] datosLeidos = c.getDatosLeerBaseDatos();
                                    for (int j = 0; j < c.getDatosLeerBaseDatos().length - 1; j++) {
                                        if (j % 2 == 0) {
                                            datosLeidos[j + 1] = jlo.getString(datosLeidos[j]);
                                        }
                                    }

                                    String[] nombresCampos = c.getDatosInsertarBaseDatos();
                                    for (int k = 0; k < datosLeidos.length; k++) {
                                        String leer = "";
                                        if (k % 2 == 0) {
                                            leer += nombresCampos[k] + ".";
                                            mostrar.append(nombresCampos[k]).append(": ");
                                        } else {
                                            leer += datosLeidos[k];
                                            mostrar.append(datosLeidos[k]).append(".\n");
                                        }
                                        ttsManager.addQueue(leer);
                                    }
                                }

                                respuesta.setText(mostrar.toString());
                                ttsManager.addQueue("Listo. ¿Qué más quieres hacer?");
                            } else {
                                ttsManager.addQueue("No hay datos para leer");
                            }
                            puedeAgradecer = true;
                            leyendo = false;
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
                            leyendo = false;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "Error :" + anError.getErrorDetail(), Toast.LENGTH_LONG).show();
                        leyendo = false;
                    }
                });
    }

    public void pedirConfirmacion(Comandos c) throws InterruptedException {
        if (esperarConfirmacion) {
            llamadoComando(c.getNumeroComando(), c, "");
        } else {
            ttsManager.addQueue("Confirma para subir los siguientes datos:");
            for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
                ttsManager.addQueue(c.getDatosInsertarBaseDatos()[i] + ".");
            }
            while (ttsManager.getIsSpeaking()) {
                Thread.sleep(1000);
            }
            iniciarEntradaVoz();
            esperarConfirmacion = true;
        }
    }

    class Hilo1 extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> finish());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that obtain the date in the moment that the user register the data
     * @return the date in the format dd/MM/yyyy-HH:mm:ss
     */
    private String date() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }

}