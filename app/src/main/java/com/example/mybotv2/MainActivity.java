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

    TextView grabar, respuesta, tvMostrarCaracteres;
    ImageButton img_btn_hablar;

    int numeroComandos, contador = 0;
    String strSpeech2Text = "", textoRepetido = "", mostrar, formandoPalabra = "",
        palabraParcial = "";
    final String strAbecedario;
    final String[] diasSemana = new String[9];
    boolean isGreeting, isGoodbyes, esperarConfirmacion, puedeAgradecer, leyendo,
            algunaBaseDatosProgreso, buscandoBaseDato, buscandoDato, deletrear = false;

    ArrayList<String> saludos = new ArrayList<>();
    ArrayList<String> saludosBot = new ArrayList<>();
    ArrayList<String> despedidas = new ArrayList<>();
    ArrayList<String> agradecimientos = new ArrayList<>();
    ArrayList<String> agradecimientosBot = new ArrayList<>();
    ArrayList<Comandos> listaComandos = new ArrayList<>();

    Map<String, String> numeros = new HashMap<>();
    Map<String, String> abecedario = new HashMap<>();

    Random rand = new Random();

    TTSManager ttsManager = null;

    {
        /* Inicializar la lista para deletrear y la conversion de letras a numeros*/
        abecedario.put("1","a"); abecedario.put("2","b"); abecedario.put("3","c");
        abecedario.put("4","d"); abecedario.put("5","e"); abecedario.put("6","f");
        abecedario.put("7","g"); abecedario.put("8","h"); abecedario.put("9","i");
        abecedario.put("10","j"); abecedario.put("11","k"); abecedario.put("12","l");
        abecedario.put("13","m"); abecedario.put("14","n"); abecedario.put("15","ñ");
        abecedario.put("16","o"); abecedario.put("17","p"); abecedario.put("18","q");
        abecedario.put("19","r"); abecedario.put("20","s"); abecedario.put("21","t");
        abecedario.put("22","u"); abecedario.put("23","v"); abecedario.put("24","w");
        abecedario.put("25","x"); abecedario.put("26","y"); abecedario.put("27","z");
        abecedario.put("28", " "); abecedario.put("29", ", "); abecedario.put("30", ".");
        abecedario.put("31", ": "); abecedario.put("32", "-"); abecedario.put("33", " - ");
        abecedario.put("34", "¿"); abecedario.put("35", "?"); abecedario.put("36", "á");
        abecedario.put("37", "é"); abecedario.put("38", "í"); abecedario.put("39", "ó");
        abecedario.put("40", "ú"); abecedario.put("41", "ü"); abecedario.put("42", "¡");
        abecedario.put("43", "!");

        mostrar = ordenarCaracteres(abecedario);
        mostrar += "Cuando termines de deletrear di 'listo'.";
        strAbecedario = mostrar;

        numeros.put("cero", "0"); numeros.put("uno", "1"); numeros.put("dos", "2");
        numeros.put("tres", "3"); numeros.put("cuatro", "4"); numeros.put("cinco", "5");
        numeros.put("seis", "6"); numeros.put("siete", "7"); numeros.put("ocho", "8");
        numeros.put("nueve", "9");

        /* Los mensajes para que el bot sepa que es un saludo y que es una despedida y que responder */
        saludos.add("hola"); saludos.add("buenos días"); saludos.add("buenas tardes");
        saludos.add("buenas noches"); saludos.add("buenas"); //saludos.add("buenos");

        saludosBot.add("Hola. ¿Que quieres hacer hoy?");
        saludosBot.add("Espero que estés teniendo un lindo día. ¿Que te gustaria hacer?");
        saludosBot.add("¡Que genial tenerte aquí!. ¿Que hacemos ahora?");
        saludosBot.add("Hola. ¡¿Con qué empezamos?!");

        despedidas.add("hasta luego"); despedidas.add("chao"); //despedidas.add("la buena");
        despedidas.add("todo bien"); despedidas.add("adiós"); despedidas.add("chau");
        despedidas.add("nos vemos"); despedidas.add("nos pi"); despedidas.add("hasta pronto");

        diasSemana[0] = "lunes"; diasSemana[1] = "martes"; diasSemana[2] = "miércoles";
        diasSemana[3] = "miercoles"; diasSemana[4] = "jueves"; diasSemana[5] = "viernes";
        diasSemana[6] = "sábado"; diasSemana[7] = "sabado"; diasSemana[8] = "domingo";

        agradecimientos.add("muchas gracias"); agradecimientos.add("gracias");
        agradecimientos.add("mi dios le pague"); agradecimientos.add("la buena");

        agradecimientosBot.add("¡Con mucho gusto!");
        agradecimientosBot.add("Para servirte");
        agradecimientosBot.add("me alegra haberte ayudado");
        agradecimientosBot.add("fue un placer");
        agradecimientosBot.add("espero seguir siendo de ayuda");
        agradecimientosBot.add("no hay porqué");
        agradecimientosBot.add("seguiré aquí por si me necesitas");
        agradecimientosBot.add("¡fue un trabajo en equipo!");
        agradecimientosBot.add("¡Estoy para ayudarte!");

        /* La lista de los comandos que reconoce la aplicación */
        listaComandos.add(new Comandos(1,
                new String[]{"fecha", "día"},
                "Si usas la palabra fecha ó día, te diré la fecha del día de hoy"));

        listaComandos.add(new Comandos(2,
                new String[]{"hora"},
                "Si usas la palabra hora, te diré la hora actual"));

        listaComandos.add(new Comandos(3, new String[]{"persona", "personas"},
                "Si usas la palabra personas, podrás agregar a alguien a la base de datos " +
                        "de las personas",
                new String[]{"Nombre", "", "Razón", "", "¿Ayudar?", ""},
                new String[]{Constants.URL_PEOPLE_FOR_KNOW_HELP_INSERT, Constants.URL_PEOPLE_FOR_KNOW_HELP_SELECT},
                new String[]{"nameperson", "", "reason", "", "help", ""},
                "Personas"));

        listaComandos.add(new Comandos(4,
                new String[]{"idea", "ideas"},
                "Si usas la palabra ideas, podrás agregar una nueva idea a la base de datos " +
                        "de las ideas de programación",
                new String[]{"Idea", "", "colaboradores", ""},
                new String[]{Constants.URL_IDEAS_FOR_APPS_INSERT, Constants.URL_IDEAS_FOR_APPS_SELECT},
                new String[]{"ideadescription", "", "collaborator", ""},
                "Ideas"));

        listaComandos.add(new Comandos(5,
                new String[]{"juego", "juegos", "rescatado", "terminado", "finalizado"},
                "Si usas la palabra juegos, rescatado ó terminado podras guardar en la " +
                        "base de datos de los juegos rescatados un nuevo juego que hayas completado",
                new String[]{"Nombre", ""},
                new String[]{Constants.URL_GAME_FINISHED_INSERT, Constants.URL_GAME_FINISHED_READ},
                new String[]{"namegame", ""},
                "Juegos rescatados"));

        listaComandos.add(new Comandos(6,
                new String[]{"película", "películas", "serie", "series", "pelicula", "peliculas"},
                "Si usas las palabras película ó serie, podrás agregar una película o una serie " +
                        "a la base de datos",
                new String[]{"Nombre", "", "Tipo", ""},
                new String[]{Constants.URL_MOVIES_OR_SERIES, Constants.URL_MOVIES_OR_SERIES_SELECT},
                new String[]{"namemovieorserie", "", "typems", ""},
                "Películas o series"));

        listaComandos.add(new Comandos(7,
                new String[]{"puedes", "sabes", "comando", "comandos", "sabe"},
                "Si usas algunas de las palabras puedes, sabes ó comandos, te diré los " +
                        "comandos sobre todo lo que puedo hacer actualmente"));

        listaComandos.add(new Comandos(8,
                new String[]{"leer", "trae", "consulta", "lee", "léeme", "leeme"},
                "Si usas alguna de las palabras, leer, trae ó consulta, te dire la " +
                        "información que haya en la base de datos de tu elección"));

        listaComandos.add(new Comandos(9,
                new String[]{"tareas", "tengo", "hay", "tarea", "pendientes", "pendiente"},
                "Si usas algunas de las palabras tareas, tengo, pendientes ó hay, podrás " +
                        "añadir una nueva cosa que tengas por hacer",
                new String[]{"Título", "", "Descripción", "", "¿Cuántos días tienes?", "", "Estado", ""},
                new String[]{Constants.URL_THINGS_TO_DO_INSERT, Constants.URL_THINGS_TO_DO_SELECT},
                new String[]{"title", "", "description", "", "datatodo", "", "status", ""},
                "Cosas pendientes"));

        listaComandos.add(new Comandos(10,
                new String[]{"sugerir", "inexistente", "inexistentes"},
                "Si usas alguna de las palabras sugerir ó inexistente podrás añadir un comando " +
                        "que te gustaría que pudiera hacer",
                new String[]{"¿Como quieres que se llame el comando?", "", "Descripción para este comando", "", "¿Cual es tú nombre?", ""},
                new String[]{Constants.URL_COMAND_NON_EXIST_INSERT, Constants.URL_COMAND_NON_EXIST_SELECT},
                new String[]{"intentcomand", "", "description", "", "whosuggestion", ""},
                "Comandos inexistentes"));

        listaComandos.add(new Comandos(11,
                new String[]{"documento", "documentos", "papeles", "archivos", "archivo"},
                "Si usas la palabra archivos, documentos o papeles, podrás añadir nuevos archivos a " +
                        "la base de datos junto con su identificador",
                new String[]{"Descripción", "", "Identificador", ""},
                new String[]{Constants.URL_FILE_INSERT, Constants.URL_FILE_SELECT},
                new String[]{"description", "", "idforfiles", ""},
                "Documentos"));

        listaComandos.add(new Comandos(12,
                new String[]{"libro", "libros"},
                "Si usas la palabra libro, podras añadir un libro leido a la base de datos",
                new String[]{"Nombre", "", "Autor", "", "Valoración", ""},
                new String[]{Constants.URL_BOOKS_INSERT, Constants.URL_BOOKS_SELECT},
                new String[]{"bookname", "", "author", "", "assessment", ""},
                "Libros"));

        listaComandos.add(new Comandos(13,
                new String[]{"cumpleaños"},
                "Si usas la palabra cumpleaños podras agregar el cumpleaños de quien tú quieras",
                new String[]{"Nombre", "", "Fecha", ""},
                new String[]{Constants.URL_BIRTHDAY_INSERT, Constants.URL_BIRTHDAY_SELECT},
                new String[]{"namebirthday", "", "date", ""},
                "Cumpleaños"));

        listaComandos.add(new Comandos(14,
                new String[]{"comprar", "por comprar", "para comprar"},
                "Si usas la palabra comprar podrás guardar algo que tengas pendiente por comprar",
                new String[]{"Nombre", "", "Categoría", "", "Costo estimado", ""},
                new String[]{Constants.URL_THINGS_TO_BUY_INSERT, Constants.URL_THINGS_TO_BUY_SELECT},
                new String[]{"namethingstobuy", "", "category", "", "estimatedcost", ""},
                "Cosas por comprar"));

        listaComandos.add(new Comandos(15,
                new String[]{"frases", "frase"},
                "Si usas la palabra frases podrás guardar una nueva frase que te haya gustado",
                new String[]{"Frase", "", "Autor", ""},
                new String[]{Constants.URL_PHRASES_INSERT, Constants.URL_PHRASES_SELECT},
                new String[]{"phrase", "", "author", ""},
                "Frases"));

        listaComandos.add(new Comandos(16,
                new String[]{"bases", "bases de datos"},
                "Si dices las palabras bases de datos podras saber todas las bases de datos " +
                        "que puedo consultar en este momento"));

        listaComandos.add(new Comandos(17,
                new String[]{"busca", "buscar"},
                "Si dices la palabra buscar podrás consultar una base de datos de tu " +
                        "elección y buscar un dato en específico"
        ));

        listaComandos.add(new Comandos(18,
                new String[]{"deletreo", "deletrear", "activar deletreo", "desactivar deletreo",
                "deletrea"},
                "Si dices activar deletreo, comenzará el protocolo que te permite formar " +
                        "oraciones deletreando y diciendo desactivar deletreo, apagarás el protocolo y podrás " +
                        "hablarme normal"
        ));
        
        listaComandos.add(new Comandos(19, 
                new String[]{"curioso", "curios", "dato curioso"},
                "Si dices la frase dato curioso puedes agregar un dato que te parezca " +
                        "interesante a la base de datos",
                new String[]{"dato", ""},
                new String[]{Constants.URL_CURIOS_DATA_INSERT, Constants.URL_CURIOS_DATA_SELECT},
                new String[]{"curiousdata", ""},
                "datos curiosos"
        ));

        listaComandos.add(new Comandos(20,
                new String[]{"chiste", "chistes"},
                "Si quieres que te cuente un chiste solo dí 'cuentame un chiste'",
                new String[]{"chiste", ""},
                new String[]{Constants.URL_SHORTJOKE_INSERT, Constants.URL_SHORTJOKE_SELECT},
                new String[]{"shortjoke", ""},
                "chistes"
        ));

        //proyectos actuales, cosas por hacer
        //Guardar actividades para realizar con fecha, tipo calendario, dato random
        numeroComandos = listaComandos.size();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());

        //Se inicializan los componentes
        grabar = findViewById(R.id.tvGrabarVoz);
        respuesta = findViewById(R.id.tvGrabarRes);
        img_btn_hablar = findViewById(R.id.img_btn_hablar);
        tvMostrarCaracteres = findViewById(R.id.tvMostrarCaracteres);
        ttsManager = new TTSManager();
        ttsManager.init(getApplicationContext());

        diferentesPalabrasClaves();

        //Cuando se presiona el imagebutton este comienza a escuchar
        img_btn_hablar.setOnClickListener(v -> iniciarEntradaVoz());
    }

    private void diferentesPalabrasClaves(){

    }

    private String ordenarCaracteres(Map<String, String> abc){
        StringBuilder newAbc = new StringBuilder();
        String i = "1";
        int i2 = 1, max = abc.size();

        while(i2 <= max){
            for (Map.Entry<String, String> entry : abc.entrySet()) {
                if(i.equals(entry.getKey())){
                    if(entry.getValue().equals(" ")){
                        newAbc.append(entry.getKey()).append(" --> ").append("(Espacio)").append("\n");
                    }else{
                        if(entry.getValue().equals(" - ")){
                            newAbc.append(entry.getKey()).append(" --> ").append("( - )").append("\n");
                        }else{
                            if(entry.getValue().equals("-")){
                                newAbc.append(entry.getKey()).append(" --> ").append("(-)").append("\n");
                            }else{
                                newAbc.append(entry.getKey()).append(" --> ").append(entry.getValue()).append("\n");
                            }
                        }
                    }
                    i2++;
                    i = i2 + "";
                }
            }
        }
        return newAbc.toString();
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

        ttsManager.stop();

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

                    strSpeech2Text = pasarPalabraNumero(strSpeech2Text);
                    strSpeech2Text = pasarDosPuntosPalabra(strSpeech2Text);
                    grabar.setText(strSpeech2Text);

                    if(desactivarDeletreo(strSpeech2Text)){
                        return;
                    }

                    //Es para activar o desactivar el deletreo y guardar palabras
                    if(buscarDatoEspecifico(strSpeech2Text)){
                        return;
                    }

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

                    //Por si no se cumplío ningún caso anterior
                    Toast.makeText(this, "No se cumplío ningún comando", Toast.LENGTH_SHORT).show();
                }

            }
    );

    private String pasarDosPuntosPalabra(String texto){
        if(texto.contains("2 puntos")){
            return texto.replace(" 2 puntos ", ": ");
        }else{
            return texto;
        }
    }

    /**
     * Este metodo pasa una palabra a numero
     * @param texto texto de lo que dijo el usuario
     * @return el texto que dijo el usuario o el numero
     */
    private String pasarPalabraNumero(String texto){
        String caracter = numeros.get(texto);
        if(caracter == null){
            return texto;
        }else{
            return caracter;
        }
    }

    /**
     * Metodo para activar o desactivar deletreo y loq que debe hacer en cada caso
     *
     * Caso 1) Lo desactiva
     * Caso 2) Lo desactiva sin haber terminado la frase: guarda la frase parcial y lo comunica al
     *         usuario para que este termine la frase de ese dato.
     * Caso 3) Activa la variable y muestra el mapa completo del abecedario
     * @param texto mensaje proveniente del usuario (Que debería decir: "deletreo")
     * @return True si se interactúo con el comando, false sino no se interactúo con el comando
     */
    public boolean desactivarDeletreo(String texto){
        if(texto.toLowerCase().contains("deletreo")){
            if(deletrear){
                deletrear = false;
                if(formandoPalabra.equals("")){
                    tvMostrarCaracteres.setText("");
                    hablando("Deletreo desactivado", 1000, true);
                }else{
                    palabraParcial = formandoPalabra;
                    hablando("Deletreo desactivado y frase parcial guardada", 1450, false);
                    hablando("La frase guardada es: " + palabraParcial, 1000, false);
                    String palabraMsj = "Frase formada: " + palabraParcial;
                    tvMostrarCaracteres.setText(palabraMsj);
                    hablando("Por favor. continúa con el resto de la frase", 2000, true);
                }
                formandoPalabra = "";
            }else{
                deletrear = true;
                ttsManager.addQueue("Deletreo activado");
                tvMostrarCaracteres.setText(strAbecedario);
            }
            return true;
        }
        return false;
    }

    /**
     * Este metodo busca si la base de datos en la que quiere buscar el usuario existe
     * @param texto mensaje proveniente del usuario (donde presuntamente debería estar el nombre
     *              de la base de datos)
     * @return true si la base de datos existe, sino retorna false
     */
    public boolean buscarDatoEspecifico(String texto){
        if(buscandoBaseDato){
            for (Comandos c : listaComandos) {
                for (String s : c.getComando()) {
                    if (texto.toLowerCase().contains(s.toLowerCase())) {
                        c.setEnProgreso(true);
                        if(c.getDatosInsertarBaseDatos() != null){
                            solicitarDatosNecesarios(c);
                            buscandoDato = true;
                            buscandoBaseDato = false;
                        }else{
                            ttsManager.addQueue("Esa base de datos no existe");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

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
     * @param texto El saludo que se reconocio por voz
     */
    public boolean saludoDespedida(String texto) {
        if (!algunaBaseDatosProgreso) {
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
                    isGreeting = true;
                    hablando(texto2Return, 1500, true);
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
            if (!isGreeting && !isGoodbyes){
                ttsManager.initQueue("Ten un poco de modales. por favor, salúdame");
                return false;
            }
        }
        return true;
    }

    /**
     * Metodo para cancelar el proceso actual
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean cancelarProceso(String texto) {
        if (leyendo || algunaBaseDatosProgreso || buscandoBaseDato || buscandoDato) {
            if (texto.contains("cance") || strSpeech2Text.toLowerCase().contains("olv")) {
                resetearDatos("Ok. Cancelado");
                puedeAgradecer = true;
                return true;
            }
            return false;
        } else {
            if (texto.contains("cance") || strSpeech2Text.toLowerCase().contains("olv")) {
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
     * Metodo que resetea los datos que se tenian almacenados y se estaban recopilando
     * (independientemente de la cantidad de datos obtenida hasta el momento) y los elimina y
     * reestablece las demás variables como 'leyendo', 'esperarConfirmacion', etc
     * @param texto Mensaje del usuario
     */
    public void resetearDatos(String texto) {
        ttsManager.addQueue(texto);
        Comandos c = mirarBaseDatosEnProgreso();
        if (c != null) {
            c.setEnProgreso(false);
            c.setConfirmacion(false);
            String[] nuevosDatos = c.getDatosInsertarBaseDatos();
            if(nuevosDatos != null){
                for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
                    if (i % 2 != 0) {
                        nuevosDatos[i] = "";
                    }
                }
                c.setDatosInsertarBaseDatos(nuevosDatos);
            }
        }

        /*assert c != null;
        c.setConfirmacion(false);*/
        algunaBaseDatosProgreso = false;
        esperarConfirmacion = false;
        leyendo = false;
        puedeAgradecer = true;
        buscandoDato = false;
        buscandoBaseDato = false;
        formandoPalabra = "";
        deletrear = false;
        palabraParcial = "";
        tvMostrarCaracteres.setText("");
    }

    /**
     * El metodo pide al usuario que rellene los datos que son necesarios para guardar un nuevo
     * registro en la base de datos
     * @param texto mensaje dicho por voz proveniente del usuario
     */
    public void agregarDatosComando(String texto) {
        Comandos c = mirarBaseDatosEnProgreso();
        boolean ciclo = true;
        int i = 0;
        try {
            //Los datos para saber que pedir y donde guardarlo
            String[] nuevosDatos = c.getDatosInsertarBaseDatos();
            /*En el ciclo se se valida si esta el modo deletreo o no
            *
            * Si esta activado, va al metodo y concatena los caracteres, hasta formar la palabra y
            * devuelve el indice 'i' a su valor anterior, ya que no se guardó ningún dato nuevo
            * luego guarda la palabra en en el vector y dice el nombre del siguiente campo a llenar
            *
            * Si esta desactivado el deletreo, entonces guarda la palabra directamente, pide la
            * siguiente y así hasta completar el vector de datos
            *
            * Si ya se llenarón los datos del vector, el ciclo se acaba y se esta buscando un dato
            * especifico entonces va a un metodo de busqueda a base de datos o a otro metodo
            *  */
            while (ciclo && i < nuevosDatos.length) {
                //Si el campo en donde van los datos está vacío
                if (i % 2 != 0 && nuevosDatos[i].equals("")) {

                    if(deletrear){
                        c.setDatosInsertarBaseDatos(nuevosDatos);
                        ciclo = false;

                        if (i < nuevosDatos.length - 1 || c.getDatosInsertarBaseDatos()[i].equals("")) {
                            nuevosDatos[i] = comandoDeletrear(texto);
                            i = (deletrear) ? (i - 1) : i;

                            if(!deletrear){
                                try{
                                    hablando(c.getDatosInsertarBaseDatos()[i + 1], 2000, true);
                                }catch (Exception ignored){}
                                deletrear = false;
                                formandoPalabra = "";
                            }
                        }
                    }else{
                        nuevosDatos[i] = palabraParcial + texto;
                        palabraParcial = "";
                        c.setDatosInsertarBaseDatos(nuevosDatos);
                        ciclo = false;

                        if (i < nuevosDatos.length - 1) {
                            hablando(c.getDatosInsertarBaseDatos()[i+1], 2000, true);
                        }
                    }

                    if (i == nuevosDatos.length - 1) {
                        ciclo = true;
                    }
                }
                i++;
            }
            if (ciclo) {
                if(buscandoDato){
                    comandoBuscarDatoEspecifico(true);
                }else{
                    comandosParaEjecutar(c.getComando()[0]);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Es para obtener el comando que agrega comandos sugeridos por el usuario
     * @param texto 'Sugerir' ya que así se invoca el comando
     * @return el objeto del comando 'sugerir comandos'
     */
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
     * Metodo para buscar el comando que el usuario solicitó
     * @param texto Mensaje del usuario
     * @return true si se usó el metodo, false si no
     */
    public boolean buscarComandoEjecutar(String texto){
        if (isGreeting) {
            comandosParaEjecutar(texto);
            return true;
        }
        return false;
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
                comandoCosasToDo(comando);
                break;
            case 10:
                comandoSugerirComandos(comando);
                break;
            case 11:
                comandoArchivos(comando);
                break;
            case 12:
                comandoLibros(comando);
                break;
            case 13:
                comandoBirthDay(comando);
                break;
            case 14:
                comandoCosasToBuy(comando);
                break;
            case 15:
                comandoFrases(comando);
                break;
            case 16:
                comandoBasesDatos();
                break;
            case 17:
                comandoBuscarDatoEspecifico(false);
                break;
            case 18:
                comandoDeletrear("");
                break;
            case 19:
                comandoDatosCuriosos(comando);
                break;
            case 20:
                comandoChistesCortos(comando);
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
                    ttsManager.addQueue("Lo siento. Eso no lo sé hacer aún. Pero si lo repites, puedo guardarlo en la base de datos");
                    contador++;
                    textoRepetido = texto;
                } else {
                    String m = "Aún no se hacer eso, pero lo guardaré en la base de datos para que " +
                            "me sea posible pronto";
                    ttsManager.addQueue(m);

                    String[] nuevosDatos = comando.getDatosInsertarBaseDatos();
                    nuevosDatos[0] = "El nombre que intentaste fue:";
                    nuevosDatos[1] = texto;
                    comando.setDatosInsertarBaseDatos(nuevosDatos);
                    comandoSugerirComandos(comando);
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

    /**
     * Inicia el proceso para solicitar datos al usuario, diciendo el primer parametro y activando
     * la entrada al metodo 'agregarDatosComando'
     * @param c Objeto Comando del que se van a tomar los parametros para solicitar los datos
     */
    public void solicitarDatosNecesarios(Comandos c) {
        for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
            if (i % 2 != 0 && c.getDatosInsertarBaseDatos()[i].equals("")) {
                algunaBaseDatosProgreso = true;
                c.setEnProgreso(true);
                respuesta.setText(c.getDatosInsertarBaseDatos()[i - 1]);
                hablando(c.getDatosInsertarBaseDatos()[i - 1], 2000, true);
                i = c.getDatosInsertarBaseDatos().length;
            }
        }
    }


    /**
     * Este metodo sirve para decirle al usuario la fecha actual con día, mes y año
     */
    private void comandoFecha() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("EEEE/dd/MM/yyyy");

        String fecha = "El día de hoy es:\n" + df.format(c.getTime());
        ttsManager.initQueue(fecha);
        respuesta.setText(fecha);
        puedeAgradecer = true;
    }

    /**
     * Este metodo sirve para decirle al usuario la hora actual
     */
    private void comandoHora() {
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

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva persona
     * @param c El comando personas
     */
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
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva idea de programación
     * @param c El comando Ideas
     */
    private void comandoIdeas(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("ideaDescription", c.getDatosInsertarBaseDatos()[1]);
            datos.put("dataIdea", date());
            datos.put("collaborator", c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos un nuevo juego rescatado
     * @param c El comando juegos rescatados
     */
    private void comandoJuegos(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("nameGame", c.getDatosInsertarBaseDatos()[1]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva película o serie
     * @param c El comando películas o series
     */
    private void comandoPeliculasSeries(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("nameMovieOrSerie", c.getDatosInsertarBaseDatos()[1]);
            datos.put("typeMS", (c.getDatosInsertarBaseDatos()[3].toLowerCase().contains("película")) ? "película" : c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para decir las funcionalidad del bot, es decir, los comandos que puede ejecutar el bot
     */
    private void comandoComandos() {
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

    /**
     * Pregunta que base de datos quiere leer y mira si existe, si es así toma el objeto y realiza
     * la consulta a la base de datos y reproduce los registros obtenidos
     * @param texto El comando que tiene las rutas de acceso a la base de datos que se quiere leer
     */
    private void comandoLeer(String texto) {
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
            hablando("¿Que base de datos quieres consultar?", 500, true);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva cosa por hacer
     * @param c El comando cosas TODO
     */
    private void comandoCosasToDo(Comandos c) {
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
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos un nuevo comando sugerido
     * @param c El comando comandos sugeridos
     */
    private void comandoSugerirComandos(Comandos c) {
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("intentComand", c.getDatosInsertarBaseDatos()[1]);
            datos.put("description", c.getDatosInsertarBaseDatos()[3]);
            datos.put("whoSuggestion", c.getDatosInsertarBaseDatos()[5]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos un nuevo archivo o papeles
     * @param c El comando archivos - papeles - documentos
     */
    private void comandoArchivos(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("description", c.getDatosInsertarBaseDatos()[1]);
            datos.put("idforfiles", c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos un nuevo libro
     * @param c El comando libros
     */
    private void comandoLibros(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("bookname", c.getDatosInsertarBaseDatos()[1]);
            datos.put("author", c.getDatosInsertarBaseDatos()[3]);
            datos.put("assessment", c.getDatosInsertarBaseDatos()[5]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos un nuevo cumpleaños
     * @param c El comando cumpleaños
     */
    private void comandoBirthDay(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("namebirthday", c.getDatosInsertarBaseDatos()[1]);
            datos.put("date", c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva cosa por comprar
     * @param c El comando de las cosas por comprar
     */
    private void comandoCosasToBuy(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("namethingstobuy", c.getDatosInsertarBaseDatos()[1]);
            datos.put("category", c.getDatosInsertarBaseDatos()[3]);
            datos.put("estimatedcost", c.getDatosInsertarBaseDatos()[5]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva frase
     * @param c El comando frases
     */
    private void comandoFrases(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("phrase", c.getDatosInsertarBaseDatos()[1]);
            datos.put("author", c.getDatosInsertarBaseDatos()[3]);
            recordGeneral(datos, c);
        }
    }

    /**
     * El metodo sirve para decir cuantas y cuales son las bases de datos a las que puede acceder el bot
     */
    private void comandoBasesDatos(){
        int nBasesDatos = 0;
        ArrayList<String> basesDatos = new ArrayList<>();
        StringBuilder res = new StringBuilder();

        for (Comandos c : listaComandos){
            if(c.getNombreBaseDatos() != null){
                nBasesDatos++;
                basesDatos.add(c.getNombreBaseDatos());
            }
        }

        ttsManager.addQueue("Por ahora existen " + nBasesDatos + " bases de datos a las que puedo acceder");
        ttsManager.addQueue("Estas son: ");

        for (int i = 0; i < basesDatos.size(); i++){
            if(i == basesDatos.size() - 1) {
                ttsManager.addQueue("Y " + basesDatos.get(i));
            }else{
                ttsManager.addQueue(basesDatos.get(i));
            }
            res.append(basesDatos.get(i)).append(". ");
        }
        respuesta.setText(res.toString());
        puedeAgradecer = true;
    }

    /**
     * El metodo sirve para buscar un dato especifico en la base de datos
     */
    private void comandoBuscarDatoEspecifico(boolean llenos){
        if(llenos){
            readSpecificDataBase(mirarBaseDatosEnProgreso());
        }else{
            buscandoBaseDato = true;
            hablando("¿En cual base de datos quieres buscar?", 1500, true);
        }
    }

    /**
     * Sirve para concatenar los caracteres elegidos por el usuario además de iniciar el deletreo
     * y sus validaciones
     * @param texto caracter
     * @return la palabra o frase completa
     */
    private String comandoDeletrear(String texto){
        if(deletrear){
            agregarCaracter(texto);

            if(texto.equals("listo")){
                deletrear = false;
                ttsManager.addQueue("La palabra formada es " + formandoPalabra);
                return formandoPalabra;
            }
        }else{
            deletrear = true;
            ttsManager.addQueue("Deletreo activado");
            tvMostrarCaracteres.setText(strAbecedario);
        }
        return "";
    }

    /**
     * Concatena los caracteres, dice el caracter elegido y muestra el progreso de la frase
     * @param txt El mensaje que proporciona el usuario presuntamente con numeros
     */
    private void agregarCaracter(String txt){
        if(txt.contains("-")){
            String[] numeros = txt.split("-");
            for (String n : numeros){
                obtenerCaracter(n.trim(), false);
            }
        }else{
            obtenerCaracter(txt, true);
        }

    }

    private void obtenerCaracter(String txtNum, boolean entradaVoz){
        try {
            int num = Integer.parseInt(txtNum);
            String caracter = abecedario.get(num + "");
            formandoPalabra += caracter;
            grabar.setText(formandoPalabra);
            hablando("Agregada la letra " + caracter, 1000, entradaVoz);
        } catch (Exception ignored) {
            if(!(txtNum.equalsIgnoreCase("listo"))){
                ttsManager.addQueue("Tienes que decir un numero de la lista");
            }
        }
    }

    /**
     * Metodo para agregar datos curiosos obtenidos mediante el usuario por voz
     * @param c Comando de los datos curiosos
     */
    private void comandoDatosCuriosos(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("curiousdata", c.getDatosInsertarBaseDatos()[1]);
            recordGeneral(datos, c);
        }
    }

    /**
     * Metodo para agregar chistes obtenidos mediante el usuario por voz
     * @param c Comando de los chistes
     */
    private void comandoChistesCortos(Comandos c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("shortJoke", c.getDatosInsertarBaseDatos()[1]);
            recordGeneral(datos, c);
        }
    }

    /**
     * Es para reproducir un mensaje para el usuario, esperar a que se acabe e iniciar una entrada de voz
     * @param mensaje Mensaje para el usuario
     * @param time Tiempo en segundo de espera para validar si ya acabó de hablar
     * @param iniciar Saber si se inicia o no la entrada de voz
     */
    private void hablando(String mensaje, int time, boolean iniciar){
        ttsManager.addQueue(mensaje);
        try {
            while (ttsManager.getIsSpeaking()) { //noinspection BusyWait
                Thread.sleep(time);
            }
        } catch (Exception ignored) {}
        if(iniciar){ iniciarEntradaVoz(); }
    }

/*
    /**
     * @param texto Texto ingresado por el ususario que contiene el día seleccionado para la consulta
     * @return ds = día de la semana
     */
    /*public String tomarDiaSeleccionado(String texto) {
        for (String ds : diasSemana) {
            if ((texto).toLowerCase().contains(ds)) {
                return ds;
            }
        }
        return "";
    }*/

    /**
     * Metodo donde se registran los datos que se tomaron del usuario
     * @param datos Mapa de los datos a guardar con sus parametros
     * @param c Objeto para tomar las rutas y parametros de donde se van a guardar los datos
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
                            tvMostrarCaracteres.setText("");
                            puedeAgradecer = true;
                        }
                    } catch (JSONException e) {
                        //Case sad with the query
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        ttsManager.addQueue("Registro no insertado");
                        resetearDatos("");
                    }
                }

                @Override
                public void onError(ANError anError) {
                    //Is execute when the query has happened an error
                    Toast.makeText(getApplicationContext(), "Error: " + anError.getErrorDetail(), Toast.LENGTH_LONG).show();
                    ttsManager.addQueue("Registro no insertado");
                    resetearDatos("");
                }
            });
        }
    }

    /**
     * Consulta una base de datos y muestra los resultados
     * @param c Comando que se eligio para consultar la base de datos
     */
    private void readDataBase(Comandos c) {
        ttsManager.addQueue("Buscando");
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
                                int cont = 0;

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jlo = array.getJSONObject(i);

                                    //JSONObject objectUser = response.getJSONObject("data");
                                    String[] datosLeidos = c.getDatosLeerBaseDatos();
                                    for (int j = 0; j < c.getDatosLeerBaseDatos().length - 1; j++) {
                                        if (j % 2 == 0) {
                                            datosLeidos[j + 1] = jlo.getString(datosLeidos[j]);
                                        }
                                    }

                                    cont++;
                                    mostrar.append(cont).append(") ");

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
                                    mostrar.append("\n");

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

    /**
     * Consulta una base de datos y muestra los resultados de los datos que coincidan con los proporcionados
     * @param c Comando que se eligio para consultar la base de datos
     */
    private void readSpecificDataBase(Comandos c) {
        ttsManager.addQueue("Buscando");
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
                                String[] datosBuscar = c.getDatosInsertarBaseDatos();
                                StringBuilder mostrar = new StringBuilder();
                                String leer = "";
                                toLowerCase(datosBuscar);
                                int l = 0, cont = 0;

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jlo = array.getJSONObject(i);

                                    //JSONObject objectUser = response.getJSONObject("data");
                                    String[] datosLeidos = c.getDatosLeerBaseDatos();
                                    for (int j = 0; j < c.getDatosLeerBaseDatos().length - 1; j++) {
                                        if (j % 2 == 0) {
                                            datosLeidos[j + 1] = jlo.getString(datosLeidos[j]);
                                        }
                                    }

                                    datosLeidos = toLowerCase(datosLeidos);
                                    if(coincidencias(datosLeidos, datosBuscar, 0)){
                                        if(l == 0){
                                            ttsManager.addQueue("Encontré estos datos que coinciden total o parcialmente con lo que buscas");
                                            hablando("", 800, false);
                                            l++;
                                        }
                                        cont++;
                                        mostrar.append(cont).append(") ");
                                        String[] nombresCampos = c.getDatosInsertarBaseDatos();
                                        for (int k = 0; k < datosLeidos.length; k++) {
                                            leer = "";
                                            if (k % 2 == 0) {
                                                leer += nombresCampos[k] + ".";
                                                mostrar.append(nombresCampos[k]).append(": ");
                                            } else {
                                                leer += datosLeidos[k];
                                                mostrar.append(datosLeidos[k]).append(".\n");
                                            }
                                            ttsManager.addQueue(leer);
                                        }
                                        mostrar.append("\n");
                                    }

                                }

                                if(leer.equals("")){
                                    ttsManager.addQueue("Lo lamento. No hay registros similares en la base de datos");
                                    ttsManager.addQueue("¿Algo más qué quieras hacer?");
                                }else{
                                    ttsManager.addQueue("Listo. ¿Qué más quieres hacer?");
                                }

                                respuesta.setText(mostrar.toString());
                            } else {
                                ttsManager.addQueue("No hay datos para leer");
                            }
                            puedeAgradecer = true;
                            resetearDatos("");
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

    /**
     * Pone todos los Strings del vector en minúsculas
     * @param datos vector de datos
     * @return retorna los datos en minúsculas
     */
    public String[] toLowerCase(String[] datos){
        for(int i = 0; i < datos.length; i++){
            datos[i] = datos[i].toLowerCase();
        }
        return datos;
    }

    /**
     * Busca las coincidencias entre los datos especificos dados por el usuario y los obtenidos de
     * la base de datos de manera recursiva para analizar todos los campos del vector
     * @param datosLeidos Los datos obtenidos mediante la consulta de la base de datos
     * @param datosBuscados Los datos proporcionados por el usuario
     * @param i Iterador para buscar solo los datos que son consulta y no los parametros de busqueda
     * @return true si hay coincidencias, false si no las hay
     */
    public boolean coincidencias(String[] datosLeidos, String[] datosBuscados, int i){
        if(i < datosLeidos.length){
            if (i % 2 != 0) {
                try{
                    if(datosLeidos[i].contains(datosBuscados[i])){
                        return true;
                    }else{
                        return coincidencias(datosLeidos, datosBuscados, i + 2);
                    }
                }catch(Exception ignored){
                    return true;
                }
            }else{
                return coincidencias(datosLeidos, datosBuscados, i + 1);
            }
        }else{
            return false;
        }
    }

    /**
     * Pide la confirmacion al usuario para saber si guarda o no los datos en la base de datos,
     * proporcionando los datos completos de lo que se va a guardar si confirma la petición
     * @param c Objeto del comando del que se van a guardar los datos en la base de datos
     * @throws InterruptedException Es para tener control por si ocurre alguna excepción
     */
    public void pedirConfirmacion(Comandos c) throws InterruptedException {
        if (esperarConfirmacion) {
            llamadoComando(c.getNumeroComando(), c, "");
        } else {
            ttsManager.addQueue("Confirma para subir los siguientes datos:");
            for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
                ttsManager.addQueue(c.getDatosInsertarBaseDatos()[i] + ".");
            }
            hablando("", 1000, true);
            esperarConfirmacion = true;
        }
    }

    /**
     * El hilo que controla el cierre de la aplicación
     */
    class Hilo1 extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1100);
                runOnUiThread(MainActivity.this::finish);
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

    /**
     * Metodo que cierra la aplicación
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }

}