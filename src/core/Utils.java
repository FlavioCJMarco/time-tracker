package core;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/** La clase Utils incluye todas las funciones que solo encajarían en el main, de modo que sean mucho más accesibles
 * desde otras clases y la estructura del código sea más clara.
 */
@SuppressWarnings("ConstantConditions")
public class Utils {

    /**
     * Creación del thread que llamará constantemente a la actualización del reloj.
     */
    public static synchronized void create_thread() {
        MyThread thread = new MyThread();
        thread.start();
    }

    /**
     * Almacenamiento de un Element (proyecto o tarea) y todos sus elementos en un fichero con formato JSON.
     */
    public static void saveJSON(Element e) throws IOException {
        assert (e != null): "Null element passed for JSON creation";

        JSONObject aux = e.createJSON();
        FileWriter file = new FileWriter("./out/production/Practica_DS/json/output.json");

        file.write(aux.toString(3));
        file.close();
    }


    /**
     * Carga de un fichero JSON y creación de un Element ("root") a partir del contenido
     * y lo muestra por pantalla si se solicita.
     */
    public static Element loadJSON(String filename, Boolean print) throws JSONException, IOException {
        assert (filename != null): "Null filename provided";
        assert (print != null): "No Boolean option provided for printing";

        filename = "./src/json/" + filename;
        String content = new String(Files.readAllBytes(Paths.get(filename)));

        JSONObject obj = new JSONObject(content);

        if (print)
            printTree(obj);

        if (obj.getString("class").equals("task")) {
            return new Task(obj);
        } else {
            return new Project(obj);
        }
    }

    public static String format_duration(Duration duration) {
        assert (duration != null): "Null duration provided";

        return String.format("%d:%02d:%02d.%03d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                duration.toMillisPart());
    }

    public static Duration duration_from_format(String str){
        assert (str != null): "Null string provided";

        String[] h_m_s = str.split(":");
        int h = Integer.parseInt(h_m_s[0]);
        int m = Integer.parseInt(h_m_s[1]);

        String[] s_ms = h_m_s[h_m_s.length-1].split("\\.");
        int s = Integer.parseInt(s_ms[0]);
        int ms = Integer.parseInt(s_ms[1]);

        Duration dur = Duration.ZERO;
        dur = dur.plusMillis(ms);
        dur = dur.plusSeconds(s);
        dur = dur.plusMinutes(m);
        dur = dur.plusHours(h);

        return dur;
    }

    public static void printTree(JSONObject tree) {
        assert (tree != null): "Null JSON tree provided";
        System.out.println(tree.toString(3));
    }

    /**
     * Esta función retorna un vector de Element al que se añaden todos los elementos que contengan
     * la tag asociada al iterador de tipo SearchByTag. Se usa de forma recursiva para acceder
     * a todos los elementos del árbol.
     */
    public static Vector<Element> getTagResult(SearchByTag it) {
        assert (it != null): "Null SearchByTag iterator provided";
        Vector<Element> results = new Vector<>();


        // PADRE:
        if (it.getM_element().containsTag(it.getTag())) {
            results.add(it.getM_element());
        }


        // HIJOS:
        if (it.hasMore()) {
            for (Element e : it.getM_element_array()) {
                SearchByTag it_next = e.createTagIterator(it.getTag().toLowerCase());
                results.addAll(getTagResult(it_next)); //addAll ya que unimos todos los miembros de un nuevo vector
            }
        }

        return results;
    }


    public static Duration TotalTime_calculation(TotalTime it){
        assert (it != null): "Null TotalTime iterator provided";
        Map<Element, Duration> pair_map = TotalTime(it);
        Duration totalTime = Duration.ZERO;
        for (Element e: pair_map.keySet()){
            if (e.getM_project().getM_name().equals("root"))
                totalTime = totalTime.plus(pair_map.get(e));
        }
        return totalTime;
    }

    public static void logTotalTimeTree(Logger LOGGER, TotalTime it){
        assert(LOGGER != null) : "Null logger provided";
        assert(it != null) : "Null TotalTime iterator provided";
        Map<Element, Duration> pair_map = TotalTime(it);
        for (Element e: pair_map.keySet()){
            milestone_LOGGER(LOGGER, "info", String.format("Elemento %s - %s", e.getM_name(),
                    format_duration(pair_map.get(e))), 2);
        }
    }

    /**
     * Esta función retorna un Map que contiene una pareja formada por un Element (key) y una duración (valor).
     * De esta manera, usándola recursivamente podemos conseguir obtener un mapa de las duraciones de todos los
     * elementos de un intervalo que nos permita loggearlas posteriormente.
     */
    public static Map<Element, Duration> TotalTime(TotalTime it) {
        assert (it != null) : "Null TotalTime iterator provided";

        Map<Element, Duration> pair_map = new HashMap<>();

        // Si el final del elemento es posterior al inicio del periodo retorna un número negativo
        // Si el inicio del elemento es posterior al fin del periodo retorna un número positivo
        if ((it.getM_element().getM_end_DateTime_e() != null) && (it.getM_element().getM_init_DateTime_e() != null))
            if (!(((it.getM_element().getM_end_DateTime_e()).compareTo(it.getM_init_DateTime()) < 0) ||
                    ((it.getM_element().getM_init_DateTime_e()).compareTo(it.getM_end_DateTime()) > 0))) {
                if (it.hasMore()) {
                    for (Element e : it.getM_element_array()) {
                        Duration aux_duration = Duration.ZERO;
                        if ((e.getM_class().equals("Task")) && (e.getM_interval_array() != null)) { /////////AÑADIDO
                            for (Interval i : e.getM_interval_array()) {

                                // INTERVALO EMPIEZA FUERA Y ACABA FUERA DEL PERIODO
                                if ((i.getM_init_DateTime_i().compareTo(it.getM_init_DateTime()) < 0) &&
                                        (i.getM_end_DateTime_i().compareTo(it.getM_end_DateTime()) > 0)) {

                                    aux_duration = aux_duration.plus(Duration.between(
                                            it.getM_init_DateTime(), it.getM_end_DateTime()));

                                }

                                // INTERVALO EMPIEZA FUERA Y ACABA DENTRO DEL PERIODO
                                if ((i.getM_init_DateTime_i().compareTo(it.getM_init_DateTime()) < 0) &&
                                        (i.getM_end_DateTime_i().compareTo(it.getM_end_DateTime()) < 0) &&
                                        (i.getM_end_DateTime_i().compareTo(it.getM_init_DateTime()) > 0)) {

                                    aux_duration = aux_duration.plus(Duration.between(it.getM_init_DateTime(),
                                            i.getM_end_DateTime_i()));

                                }

                                // INTERVALO EMPIEZA DENTRO Y ACABA FUERA DEL PERIODO
                                if ((i.getM_init_DateTime_i().compareTo(it.getM_init_DateTime()) > 0) &&
                                        (i.getM_end_DateTime_i().compareTo(it.getM_end_DateTime()) > 0) &&
                                        ((i.getM_init_DateTime_i()).compareTo(it.getM_end_DateTime()) < 0)) {

                                    aux_duration = aux_duration.plus(Duration.between(i.getM_init_DateTime_i(),
                                            it.getM_end_DateTime()));

                                }

                                // INTERVALO EMPIEZA DENTRO Y ACABA DENTRO DEL PERIODO
                                if ((i.getM_init_DateTime_i().compareTo(it.getM_init_DateTime()) > 0) &&
                                        (i.getM_end_DateTime_i().compareTo(it.getM_end_DateTime()) < 0)) {

                                    aux_duration = aux_duration.plus(Duration.between(i.getM_init_DateTime_i(),
                                            i.getM_end_DateTime_i()));

                                }

                            }
                        } else {

                            // Si el Element e está en pair_map...
                            if (pair_map.get(e) != null) {
                                // ... modificamos la duración de e en pair_map con la suma de la duración que tenía
                                // anteriormente y la nueva duración calculada.
                                pair_map.put(e, aux_duration.plus(pair_map.get(e)));
                            } else { // Si el Element e NO está en pair_map...
                                // Añadimos e al pair_map con la nueva duración calculada
                                // (o 0 si está fuera del periodo).
                                pair_map.put(e, aux_duration);
                            }

                            // Creamos un iterador de tipo TotalTime a partir del Element e para analizar
                            // a sus posibles hijos.
                            TotalTime it_next = e.createTotalTimeIterator(it.getM_init_DateTime(),
                                    it.getM_end_DateTime());
                            // Añadimos al pair_map todos los pares de element+duration correspondientes
                            // a los hijos del Element e.
                            pair_map.putAll(TotalTime(it_next));

                            // Recorremos cada Element del pair_map para comprobar si contiene Element hijos (e2)
                            // del Element e cuya duración se pueda sumar a aux_duration para luego añadírsela
                            // a la del Element e (ya que la duración de los padres es la duración de sus hijos).
                            for (Element e2 : pair_map.keySet()) {
                                // Si el padre del Element e2 es el Element e añadimos la duración del Element e2
                                // a aux_duration, que luego se añadirá a la del Element e.
                                if (e2.getM_project().equals(e)) {
                                    if (aux_duration != null)
                                        aux_duration = aux_duration.plus(pair_map.get(e2));
                                    }
                                }
                            }

                        // Se vuelve a modificar o añadir el Element e como al principio del else, pero con aux_duration
                        // actualizada con las duraciones de los hijos de e.
                        if (pair_map.get(e) != null)
                            pair_map.put(e, aux_duration.plus(pair_map.get(e)));
                        else
                            pair_map.put(e, aux_duration);
                        }
                    }
                }
        return pair_map;
    }


    public static void milestone_LOGGER(Logger LOGGER, String level, String s, int milestone){
        assert (LOGGER != null): "Null logger provided";
        assert (level != null): "Null logger level provided";
        assert (s != null): "Null string provided to log";
        assert ((milestone == 1) || (milestone == 2)): "Non-existent milestone provided";
        // Change (||) by (&&) to test assertions

        Boolean log_milestone1 = Boolean.TRUE; // En la aplicación final serán gets
        Boolean log_milestone2 = Boolean.TRUE;

        //noinspection ConstantConditions
        if (((log_milestone1) && (milestone == 1)) || ((log_milestone2) && (milestone == 2))){
            if (level.equals("info"))
                LOGGER.info(s);
            if (level.equals("warn"))
                LOGGER.warn(s);
            if (level.equals("debug"))
                LOGGER.debug(s);
        }


    }
}
