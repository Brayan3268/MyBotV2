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
import com.example.mybotv2.classMne.Comando;
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
        palabraParcial = "", ultimaRespuestaBot = "";
    String strAbecedario;
    String[] diasSemana = new String[9];
    boolean isGreeting, isGoodbyes, esperarConfirmacion, puedeAgradecer, leyendo,
            algunaBaseDatosProgreso, buscandoBaseDato, buscandoDato, deletrear = false;

    ArrayList<String> saludos = new ArrayList<>();
    ArrayList<String> saludosBot = new ArrayList<>();
    ArrayList<String> despedidas = new ArrayList<>();
    ArrayList<String> agradecimientos = new ArrayList<>();
    ArrayList<String> agradecimientosBot = new ArrayList<>();
    ArrayList<Comando> listaComandos = new ArrayList<>();

    Map<String, String> numeros = new HashMap<>();
    Map<String, String> abecedario = new HashMap<>();

    Random rand = new Random();

    TTSManager ttsManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());

        //Se inicializan los componentes
        Constants.inicializar();
        agradecimientos = Constants.agradecimientos;
        agradecimientosBot = Constants.agradecimientosBot;
        despedidas = Constants.despedidas;
        saludosBot = Constants.saludosBot;
        saludos = Constants.saludos;
        numeros = Constants.numeros;
        diasSemana = Constants.diasSemana;
        abecedario = Constants.abecedario;
        mostrar = Constants.mostrar;
        strAbecedario = Constants.strAbecedario;
        listaComandos = Constants.listaComandos;
        numeroComandos = listaComandos.size();

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
                hablando("Deletreo activado", 0, false);
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
            for (Comando c : listaComandos) {
                for (String s : c.getComando()) {
                    if (texto.toLowerCase().contains(s.toLowerCase())) {
                        c.setEnProgreso(true);
                        if(c.getDatosInsertarBaseDatos() != null){
                            solicitarDatosNecesarios(c);
                            buscandoDato = true;
                            buscandoBaseDato = false;
                        }else{
                            hablando("Esa base de datos no existe", 0, false);
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
                hablando(texto2Return, 0, false);
                puedeAgradecer = false;
                return true;
            }

            //Por si no hay nada que agradecer
            if(texto.contains(agradecimiento) && !puedeAgradecer){
                String res = "¡No hay nada que agradecer!";
                respuesta.setText(res);
                hablando(res, 0, false);
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
                    hablando("Cuídate", 0, false);
                    isGoodbyes = true;
                    new Hilo1().start();
                    return false;
                }
            }

            //Se mira si se saludo o se despedio para dar un mensaje al usuario insitandolo a saludar
            if (!isGreeting && !isGoodbyes){
                hablando("Ten un poco de modales. por favor, salúdame", 1000, true);
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
                hablando("No hay procesos para cancelar", 0, false);
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
                    Comando c = mirarBaseDatosEnProgreso();
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
        hablando(texto, 0, false);
        Comando c = mirarBaseDatosEnProgreso();
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
        Comando c = mirarBaseDatosEnProgreso();
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
    public Comando obtenerComandoPeticion(String texto) {
        for (Comando c : listaComandos) {
            for (String s : c.getComando()) {
                if (texto.toLowerCase().contains(s.toLowerCase())) {
                    return (c);
                }
            }
        }
        return new Comando();
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
        for (Comando c : listaComandos) {
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
    public void llamadoComando(int comandoParaEjecutar, Comando comando, String texto) {
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
            case 21:
                comandoRepetir();
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
                    hablando("Lo siento. Eso no lo sé hacer aún. Pero si lo repites, puedo guardarlo en la base de datos", 0, false);
                    contador++;
                    textoRepetido = texto;
                } else {
                    hablando("Aún no se hacer eso, pero lo guardaré en la base de datos para que " +
                            "me sea posible pronto", 500, false);

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
    public Comando mirarBaseDatosEnProgreso() {
        for (Comando c : listaComandos) {
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
    public void solicitarDatosNecesarios(Comando c) {
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
        hablando(fecha, 0, false);
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
        if (hora < 12) {
            horaCompleta = hora != 1 ? "Son las " + hora + " y " + minuto + " de la mañana" : "Son la una y " + minuto + " de la mañana";
        } else {
            if(hora == 12){
                horaCompleta = "Son las " + hora + " y " + minuto + " de la tarde";
            }else{
                if (hora <= 18) {
                    horaCompleta = hora != 13 ? "Son las " + (hora - 12) + " y " + minuto + " de la tarde" : "Son la una y " + minuto + " de la tarde";
                } else {
                    if(hora < 24){
                        horaCompleta = "Son las " + (hora - 12) + " y " + minuto + " de la noche";
                    }else{
                        horaCompleta = "Son las " + (hora - 12) + " y " + minuto + " de la madrugada";
                    }
                }
            }
        }
        hablando(horaCompleta, 0, false);
        respuesta.setText(horaCompleta);
        puedeAgradecer = true;
    }

    /**
     * El metodo sirve para pedir datos y guardar en la base de datos una nueva persona
     * @param c El comando personas
     */
    private void comandoPersonas(Comando c) {
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
    private void comandoIdeas(Comando c) {
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
    private void comandoJuegos(Comando c) {
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
    private void comandoPeliculasSeries(Comando c) {
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
        for (Comando c : listaComandos) {
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
            hablando(c.getDescripcion(), 0, false);
        }
        respuesta.setText(comandos.toString());
        hablando("Y bien. ¿Que quieres hacer primero?", 0, false);
        puedeAgradecer = true;
    }

    /**
     * Pregunta que base de datos quiere leer y mira si existe, si es así toma el objeto y realiza
     * la consulta a la base de datos y reproduce los registros obtenidos
     * @param texto El comando que tiene las rutas de acceso a la base de datos que se quiere leer
     */
    private void comandoLeer(String texto) {
        if (leyendo) {
            for (Comando c : listaComandos) {
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
    private void comandoCosasToDo(Comando c) {
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
    private void comandoSugerirComandos(Comando c) {
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
    private void comandoArchivos(Comando c){
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
    private void comandoLibros(Comando c){
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
    private void comandoBirthDay(Comando c){
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
    private void comandoCosasToBuy(Comando c){
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
    private void comandoFrases(Comando c){
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

        for (Comando c : listaComandos){
            if(c.getNombreBaseDatos() != null){
                nBasesDatos++;
                basesDatos.add(c.getNombreBaseDatos());
            }
        }

        hablando("Por ahora existen " + nBasesDatos + " bases de datos a las que puedo acceder." + "Estas son: ", 0, false);

        for (int i = 0; i < basesDatos.size(); i++){
            if(i == basesDatos.size() - 1) {
                hablando("Y " + basesDatos.get(i), 0, false);
            }else{
                hablando(basesDatos.get(i), 0, false);
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
                hablando("La palabra formada es " + formandoPalabra, 0, false);
                return formandoPalabra;
            }
        }else{
            deletrear = true;
            hablando("Deletreo activado", 0, false);
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
                hablando("Tienes que decir un número de la lista", 0, false);
            }
        }
    }

    /**
     * Metodo para agregar datos curiosos obtenidos mediante el usuario por voz
     * @param c Comando de los datos curiosos
     */
    private void comandoDatosCuriosos(Comando c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("curiousData", c.getDatosInsertarBaseDatos()[1]);
            recordGeneral(datos, c);
        }
    }

    /**
     * Metodo para agregar chistes obtenidos mediante el usuario por voz
     * @param c Comando de los chistes
     */
    private void comandoChistesCortos(Comando c){
        solicitarDatosNecesarios(c);
        if (c.getDatosInsertarBaseDatos()[c.getDatosInsertarBaseDatos().length - 1].length() != 0 && c.getEnProgreso()) {
            Map<String, String> datos = new HashMap<>();
            datos.put("shortJoke", c.getDatosInsertarBaseDatos()[1]);
            recordGeneral(datos, c);
        }
    }

    /**
     * Método que hace que Celeste repita lo último que dijo
     */
    private void comandoRepetir(){
        hablando(ultimaRespuestaBot, 1000, false);
    }

    /**
     * Es para reproducir un mensaje para el usuario, esperar a que se acabe e iniciar una entrada de voz
     * @param mensaje Mensaje para el usuario
     * @param time Tiempo en segundo de espera para validar si ya acabó de hablar
     * @param iniciar Saber si se inicia o no la entrada de voz
     */
    private void hablando(String mensaje, int time, boolean iniciar){
        if(ttsManager.getIsLoaded()){
            ttsManager.addQueue(mensaje);
        }else{
            ttsManager.initQueue(mensaje);
        }
        ultimaRespuestaBot = mensaje;

        if(time != 0){
            try {
                while (ttsManager.getIsSpeaking()) { //noinspection BusyWait
                    Thread.sleep(time);
                }
            } catch (Exception ignored) {}
            if(iniciar){ iniciarEntradaVoz(); }
        }
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
    private void recordGeneral(Map<String, String> datos, Comando c) {
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
                            hablando("Ha ocurrido un error. El error es: " + error, 0, false);
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
                        hablando("Registro no insertado en la base de datos", 0, false);
                        resetearDatos("");
                    }
                }

                @Override
                public void onError(ANError anError) {
                    //Is execute when the query has happened an error
                    Toast.makeText(getApplicationContext(), "Error: " + anError.getErrorDetail(), Toast.LENGTH_LONG).show();
                    hablando("Registro no insertado", 0, false);
                    resetearDatos("");
                }
            });
        }
    }

    /**
     * Consulta una base de datos y muestra los resultados
     * @param c Comando que se eligio para consultar la base de datos
     */
    private void readDataBase(Comando c) {
        hablando("Buscando", 0, false);
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
                                hablando("Los datos son: ", 0, false);
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
                                        hablando(leer, 0, false);
                                    }
                                    mostrar.append("\n");

                                }

                                respuesta.setText(mostrar.toString());
                                hablando("Listo. ¿Qué más quieres hacer?", 0, false);
                            } else {
                                hablando("No hay datos para leer", 0, false);
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
    private void readSpecificDataBase(Comando c) {
        hablando("Buscando", 0, false);
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
                                            hablando("Encontré estos datos que coinciden total o parcialmente con lo que buscas.", 800, false);
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
                                            hablando(leer, 0, false);
                                        }
                                        mostrar.append("\n");
                                    }

                                }

                                if(leer.equals("")){
                                    hablando("Lo lamento. No hay registros similares en la " +
                                            "base de datos. ¿Algo más qué quieras hacer?", 0, false);
                                }else{
                                    hablando("Listo. ¿Qué más quieres hacer?", 0, false);
                                }
                                respuesta.setText(mostrar.toString());
                            } else {
                                hablando("No hay datos en la base de datos", 0, false);
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
    public void pedirConfirmacion(Comando c) throws InterruptedException {
        if (esperarConfirmacion) {
            llamadoComando(c.getNumeroComando(), c, "");
        } else {
            hablando("Confirma para subir los siguientes datos: ", 0, false);
            for (int i = 0; i < c.getDatosInsertarBaseDatos().length; i++) {
                hablando(c.getDatosInsertarBaseDatos()[i] + ".", 0, false);
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