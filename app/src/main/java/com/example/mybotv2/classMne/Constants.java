package com.example.mybotv2.classMne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {

    private final static String URL_WEB_SERVICE = "https://quiet-coast-20436.herokuapp.com/";

    public final static String URL_PEOPLE_FOR_KNOW_HELP_INSERT = URL_WEB_SERVICE + "peopleForKnowHelp_INSERTAR.php";
    public final static String URL_IDEAS_FOR_APPS_INSERT = URL_WEB_SERVICE + "ideasForApps_INSERT.php";
    public final static String URL_GAME_FINISHED_INSERT = URL_WEB_SERVICE + "game_finished_INSERT.php";
    public final static String URL_GAME_FINISHED_READ = URL_WEB_SERVICE + "game_finished_SELECT.php";
    public final static String URL_MOVIES_OR_SERIES = URL_WEB_SERVICE + "movie_or_serie_INSERT.php";
    public final static String URL_PEOPLE_FOR_KNOW_HELP_SELECT = URL_WEB_SERVICE + "peopleForKnowHelp_SELECT.php";
    public final static String URL_MOVIES_OR_SERIES_SELECT = URL_WEB_SERVICE + "movie_or_serie_SELECT.php";
    public final static String URL_IDEAS_FOR_APPS_SELECT = URL_WEB_SERVICE + "ideasForApps_SELECT.php";
    public final static String URL_THINGS_TO_DO_INSERT = URL_WEB_SERVICE + "things_to_do_INSERT.php";
    public final static String URL_THINGS_TO_DO_SELECT = URL_WEB_SERVICE + "things_to_do_SELECT.php";
    public final static String URL_COMAND_NON_EXIST_INSERT = URL_WEB_SERVICE + "comand_non_exist_INSERT.php";
    public final static String URL_COMAND_NON_EXIST_SELECT = URL_WEB_SERVICE + "comand_non_exist_SELECT.php";
    public final static String URL_FILE_INSERT = URL_WEB_SERVICE + "file_INSERT.php";
    public final static String URL_FILE_SELECT = URL_WEB_SERVICE + "file_SELECT.php";
    public final static String URL_BOOKS_INSERT = URL_WEB_SERVICE + "book_INSERT.php";
    public final static String URL_BOOKS_SELECT = URL_WEB_SERVICE + "book_SELECT.php";
    public final static String URL_BIRTHDAY_INSERT = URL_WEB_SERVICE + "birthday_INSERT.php";
    public final static String URL_BIRTHDAY_SELECT = URL_WEB_SERVICE + "birthday_SELECT.php";
    public final static String URL_THINGS_TO_BUY_INSERT = URL_WEB_SERVICE + "things_to_buy_INSERT.php";
    public final static String URL_THINGS_TO_BUY_SELECT = URL_WEB_SERVICE + "things_to_buy_SELECT.php";
    public final static String URL_PHRASES_INSERT = URL_WEB_SERVICE + "phrase_INSERT.php";
    public final static String URL_PHRASES_SELECT = URL_WEB_SERVICE + "phrase_SELECT.php";
    public final static String URL_CURIOS_DATA_INSERT = URL_WEB_SERVICE + "curious_data_INSERT.php";
    public final static String URL_CURIOS_DATA_SELECT = URL_WEB_SERVICE + "curious_data_SELECT.php";
    public final static String URL_SHORTJOKE_INSERT = URL_WEB_SERVICE + "shortJoke_INSERT.php";
    public final static String URL_SHORTJOKE_SELECT = URL_WEB_SERVICE + "shortJoke_SELECT.php";

    public static ArrayList<String> agradecimientos = new ArrayList<>();
    public static ArrayList<String> agradecimientosBot = new ArrayList<>();
    public static ArrayList<String> despedidas = new ArrayList<>();
    public static ArrayList<String> saludosBot = new ArrayList<>();
    public static ArrayList<String> saludos = new ArrayList<>();
    public static ArrayList<Comando> listaComandos = new ArrayList<>();

    public static Map<String, String> numeros = new HashMap<>();
    public static Map<String, String> abecedario = new HashMap<>();

    public static String[] diasSemana = new String[9];
    public static String strAbecedario, mostrar;

    public static void inicializar(){
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

        despedidas.add("hasta luego"); despedidas.add("chao"); //despedidas.add("la buena");
        despedidas.add("todo bien"); despedidas.add("adiós"); despedidas.add("chau");
        despedidas.add("nos vemos"); despedidas.add("nos pi"); despedidas.add("hasta pronto");

        saludosBot.add("Hola. ¿Que quieres hacer hoy?");
        saludosBot.add("Espero que estés teniendo un lindo día. ¿Que te gustaria hacer?");
        saludosBot.add("¡Que genial tenerte aquí!. ¿Que hacemos ahora?");
        saludosBot.add("Hola. ¡¿Con qué empezamos?!");

        /* Los mensajes para que el bot sepa que es un saludo y que es una despedida y que responder */
        saludos.add("hola"); saludos.add("buenos días"); saludos.add("buenas tardes");
        saludos.add("buenas noches"); saludos.add("buenas"); //saludos.add("buenos");
        saludos.add("oe"); saludos.add("bien o qué"); saludos.add("celeste");

        numeros.put("cero", "0"); numeros.put("uno", "1"); numeros.put("dos", "2");
        numeros.put("tres", "3"); numeros.put("cuatro", "4"); numeros.put("cinco", "5");
        numeros.put("seis", "6"); numeros.put("siete", "7"); numeros.put("ocho", "8");
        numeros.put("nueve", "9");

        diasSemana[0] = "lunes"; diasSemana[1] = "martes"; diasSemana[2] = "miércoles";
        diasSemana[3] = "miercoles"; diasSemana[4] = "jueves"; diasSemana[5] = "viernes";
        diasSemana[6] = "sábado"; diasSemana[7] = "sabado"; diasSemana[8] = "domingo";

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

        /* La lista de los comandos que reconoce la aplicación */
        listaComandos.add(new Comando(1,
                new String[]{"fecha", "día"},
                "Si usas la palabra fecha ó día, te diré la fecha del día de hoy"));

        listaComandos.add(new Comando(2,
                new String[]{"hora"},
                "Si usas la palabra hora, te diré la hora actual"));

        listaComandos.add(new Comando(3, new String[]{"persona", "personas"},
                "Si usas la palabra personas, podrás agregar a alguien a la base de datos " +
                        "de las personas",
                new String[]{"Nombre", "", "Razón", "", "¿Ayudar?", ""},
                new String[]{Constants.URL_PEOPLE_FOR_KNOW_HELP_INSERT, Constants.URL_PEOPLE_FOR_KNOW_HELP_SELECT},
                new String[]{"nameperson", "", "reason", "", "help", ""},
                "Personas"));

        listaComandos.add(new Comando(4,
                new String[]{"idea", "ideas"},
                "Si usas la palabra ideas, podrás agregar una nueva idea a la base de datos " +
                        "de las ideas de programación",
                new String[]{"Idea", "", "colaboradores", ""},
                new String[]{Constants.URL_IDEAS_FOR_APPS_INSERT, Constants.URL_IDEAS_FOR_APPS_SELECT},
                new String[]{"ideadescription", "", "collaborator", ""},
                "Ideas"));

        listaComandos.add(new Comando(5,
                new String[]{"juego", "juegos", "rescatado", "terminado", "finalizado"},
                "Si usas la palabra juegos, rescatado ó terminado podras guardar en la " +
                        "base de datos de los juegos rescatados un nuevo juego que hayas completado",
                new String[]{"Nombre", ""},
                new String[]{Constants.URL_GAME_FINISHED_INSERT, Constants.URL_GAME_FINISHED_READ},
                new String[]{"namegame", ""},
                "Juegos rescatados"));

        listaComandos.add(new Comando(6,
                new String[]{"película", "películas", "serie", "series", "pelicula", "peliculas"},
                "Si usas las palabras película ó serie, podrás agregar una película o una serie " +
                        "a la base de datos",
                new String[]{"Nombre", "", "Tipo", ""},
                new String[]{Constants.URL_MOVIES_OR_SERIES, Constants.URL_MOVIES_OR_SERIES_SELECT},
                new String[]{"namemovieorserie", "", "typems", ""},
                "Películas o series"));

        listaComandos.add(new Comando(7,
                new String[]{"puedes", "sabes", "comando", "comandos", "sabe"},
                "Si usas algunas de las palabras puedes, sabes ó comandos, te diré los " +
                        "comandos sobre todo lo que puedo hacer actualmente"));

        listaComandos.add(new Comando(8,
                new String[]{"leer", "trae", "consulta", "lee", "léeme", "leeme"},
                "Si usas alguna de las palabras, leer, trae ó consulta, te dire la " +
                        "información que haya en la base de datos de tu elección"));

        listaComandos.add(new Comando(9,
                new String[]{"tareas", "tengo", "hay", "tarea", "pendientes", "pendiente"},
                "Si usas algunas de las palabras tareas, tengo, pendientes ó hay, podrás " +
                        "añadir una nueva cosa que tengas por hacer",
                new String[]{"Título", "", "Descripción", "", "¿Cuántos días tienes?", "", "Estado", ""},
                new String[]{Constants.URL_THINGS_TO_DO_INSERT, Constants.URL_THINGS_TO_DO_SELECT},
                new String[]{"title", "", "description", "", "datatodo", "", "status", ""},
                "Cosas pendientes"));

        listaComandos.add(new Comando(10,
                new String[]{"sugerir", "inexistente", "inexistentes"},
                "Si usas alguna de las palabras sugerir ó inexistente podrás añadir un comando " +
                        "que te gustaría que pudiera hacer",
                new String[]{"¿Como quieres que se llame el comando?", "", "Descripción para este comando", "", "¿Cual es tú nombre?", ""},
                new String[]{Constants.URL_COMAND_NON_EXIST_INSERT, Constants.URL_COMAND_NON_EXIST_SELECT},
                new String[]{"intentcomand", "", "description", "", "whosuggestion", ""},
                "Comandos inexistentes"));

        listaComandos.add(new Comando(11,
                new String[]{"documento", "documentos", "papeles", "archivos", "archivo"},
                "Si usas la palabra archivos, documentos o papeles, podrás añadir nuevos archivos a " +
                        "la base de datos junto con su identificador",
                new String[]{"Descripción", "", "Identificador", ""},
                new String[]{Constants.URL_FILE_INSERT, Constants.URL_FILE_SELECT},
                new String[]{"description", "", "idforfiles", ""},
                "Documentos"));

        listaComandos.add(new Comando(12,
                new String[]{"libro", "libros"},
                "Si usas la palabra libro, podras guardar un nuevo libro que haya leido a la base de datos",
                new String[]{"Nombre", "", "Autor", "", "Valoración", ""},
                new String[]{Constants.URL_BOOKS_INSERT, Constants.URL_BOOKS_SELECT},
                new String[]{"bookname", "", "author", "", "assessment", ""},
                "Libros"));

        listaComandos.add(new Comando(13,
                new String[]{"cumpleaños"},
                "Si usas la palabra cumpleaños podras agregar el cumpleaños de quien tú quieras",
                new String[]{"Nombre", "", "Fecha", ""},
                new String[]{Constants.URL_BIRTHDAY_INSERT, Constants.URL_BIRTHDAY_SELECT},
                new String[]{"namebirthday", "", "date", ""},
                "Cumpleaños"));

        listaComandos.add(new Comando(14,
                new String[]{"comprar", "por comprar", "para comprar"},
                "Si usas la palabra comprar podrás guardar algo que tengas pendiente por comprar",
                new String[]{"Nombre", "", "Categoría", "", "Costo estimado", ""},
                new String[]{Constants.URL_THINGS_TO_BUY_INSERT, Constants.URL_THINGS_TO_BUY_SELECT},
                new String[]{"namethingstobuy", "", "category", "", "estimatedcost", ""},
                "Cosas por comprar"));

        listaComandos.add(new Comando(15,
                new String[]{"frases", "frase"},
                "Si usas la palabra frases podrás guardar una nueva frase que te haya gustado",
                new String[]{"Frase", "", "Autor", ""},
                new String[]{Constants.URL_PHRASES_INSERT, Constants.URL_PHRASES_SELECT},
                new String[]{"phrase", "", "author", ""},
                "Frases"));

        listaComandos.add(new Comando(16,
                new String[]{"bases", "bases de datos"},
                "Si dices las palabras bases de datos podras saber todas las bases de datos " +
                        "que puedo consultar en este momento"));

        listaComandos.add(new Comando(17,
                new String[]{"busca", "buscar"},
                "Si dices la palabra buscar podrás consultar una base de datos de tu " +
                        "elección y buscar un dato en específico"
        ));

        listaComandos.add(new Comando(18,
                new String[]{"deletreo", "deletrear", "activar deletreo", "desactivar deletreo",
                        "deletrea"},
                "Si dices activar deletreo, comenzará el protocolo que te permite formar " +
                        "oraciones deletreando y diciendo desactivar deletreo, apagarás el protocolo y podrás " +
                        "hablarme normal"
        ));

        listaComandos.add(new Comando(19,
                new String[]{"curioso", "curios", "dato curioso"},
                "Si dices la frase dato curioso puedes agregar un dato que te parezca " +
                        "interesante a la base de datos",
                new String[]{"dato", ""},
                new String[]{Constants.URL_CURIOS_DATA_INSERT, Constants.URL_CURIOS_DATA_SELECT},
                new String[]{"curiousdata", ""},
                "datos curiosos"
        ));

        listaComandos.add(new Comando(20,
                new String[]{"chiste", "chistes"},
                "Si quieres que te cuente un chiste solo dí 'cuentame un chiste'",
                new String[]{"chiste", ""},
                new String[]{Constants.URL_SHORTJOKE_INSERT, Constants.URL_SHORTJOKE_SELECT},
                new String[]{"shortjoke", ""},
                "chistes"
        ));

        listaComandos.add(new Comando(21,
                new String[]{"repetir", "repite", "repite eso"},
                "Si dices repetir, volveré a decirte lo último que ya te había dicho"));

        //proyectos actuales, cosas por hacer
        //Guardar actividades para realizar con fecha, tipo calendario, dato random
    }

    private static String ordenarCaracteres(Map<String, String> abc){
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

}