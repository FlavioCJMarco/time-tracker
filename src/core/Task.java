package core;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Vector;

/**
 * La clase task representa el nivel intermedio del árbol (project-TASK-interval).
 * Las tasks están formadas por intervalos y pueden ser independientes o depender de un proyecto.
 * Es un Element junto con Project (patrón Composite).
 *
 * Puede crearse una task a partir del nombre (y opcionalmente un proyecto padre) o a partir
 * de un JSONObject (y opcionalmente un proyecto padre).
 *
 * Tiene un array de Interval que almacena todos sus intervalos.
 *
 * Cada Task es encargada de crear y detener cada intervalo dependiente de ella.
 *
 * También cuenta con setters, getters y métodos para trasladar las variables a formato JSON.
 */
public class Task extends Element {


    private Vector<Interval> m_interv_array;
    protected Vector<String> m_tag_array;
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

// --Commented out by Inspection START (03/11/2020 19:23):
//    public Task(String name) {
//        m_name = name;
//        m_duration_e = Duration.ZERO;
//        m_class = "task";
//        m_active = Boolean.FALSE;
//    }
// --Commented out by Inspection STOP (03/11/2020 19:23)

    public Task(String name, Project project_padre) {
        assert (name != null): "Null name provided";
        assert (project_padre != null): "Null father project provided";

        m_name = name;
        m_id = IdHandler.getNewId();
        m_duration_e = Duration.ZERO;
        m_project = project_padre;
        m_class = "Task";
        m_active = Boolean.FALSE;

        m_init_DateTime_e = null;
        m_end_DateTime_e = null;
        m_tag_array = new Vector<>();
    }





    public Task (Project project_padre, JSONObject json_loader){
        assert (project_padre != null): "Null father project provided";
        assert (json_loader != null): "Null JSON loader provided";

        m_project = project_padre;

        m_name = json_loader.getString("name");
        m_id = json_loader.getInt("id");
        m_duration_e = Utils.duration_from_format(json_loader.getString("duration"));
        m_class = json_loader.getString("class");
        m_active = json_loader.getBoolean("active");

        if (json_loader.has("init_time")) {
            m_init_DateTime_e = LocalDateTime.parse(json_loader.getString("init_time"));
        }

        if (json_loader.has("end_time")) {
            m_end_DateTime_e = LocalDateTime.parse(json_loader.getString("end_time"));
        }

        if (json_loader.has("intervals")) {
            JSONArray json_loader_intervals = json_loader.getJSONArray("intervals");
            JSONObject obj;

            m_interv_array = new Vector<>();
            m_tag_array = new Vector<>();


            for (int i = 0; i < json_loader_intervals.length(); i++){
                obj = json_loader_intervals.getJSONObject(i);
                Interval loaded_interval = new Interval(this, obj);

                loaded_interval.setID(IdHandler.getNewId());
                m_interv_array.add(loaded_interval);
            }

        }
    }

    public Task (JSONObject json_loader){
        assert (json_loader != null): "Null JSON loader provided";

        m_name = json_loader.getString("name");
        m_id = json_loader.getInt("id");
        m_duration_e = Utils.duration_from_format(json_loader.getString("duration"));
        m_class = json_loader.getString("class");
        m_active = json_loader.getBoolean("active");

        if (json_loader.has("init_time")) {
            m_init_DateTime_e = LocalDateTime.parse(json_loader.getString("init_time"));
        }

        if (json_loader.has("end_time")) {
            m_end_DateTime_e = LocalDateTime.parse(json_loader.getString("end_time"));
        }

        if (json_loader.has("intervals")) {
            JSONArray json_loader_intervals = json_loader.getJSONArray("intervals");
            JSONObject obj;

            m_interv_array = new Vector<>();
            m_tag_array = new Vector<>();


            for (int i = 0; i < json_loader_intervals.length(); i++){
                obj = json_loader_intervals.getJSONObject(i);
                Interval loaded_interval = new Interval(this, obj);

                loaded_interval.setID(IdHandler.getNewId());
                m_interv_array.add(loaded_interval);
            }
        }
    }


    public void createInterval() {
        if (!m_active) {
            if (m_interv_array == null) {
                ClockTimer clock = ClockTimer.getClockTimer();
                m_interv_array = new Vector<>();
                m_init_DateTime_e = clock.now();
            }
            Utils.milestone_LOGGER(LOGGER, "warn", m_name + " starts", 1);
            Interval interv_aux = new Interval(this);
            m_interv_array.add(interv_aux);
            m_active = true;
        }
    }

    public void stopInterval(){
        Utils.milestone_LOGGER(LOGGER, "warn", m_name+" stops", 1);
        ClockTimer clock = ClockTimer.getClockTimer();
        Interval interval=m_interv_array.lastElement();
        interval.stopListening();
        m_end_DateTime_e=clock.now();
        m_active = false;

        if (m_project != null){
            m_project.update(m_end_DateTime_e);
        }

    }

    public void update(LocalDateTime arg){
        assert (arg != null): "Null arg (end time) provided for updating";

        m_end_DateTime_e = arg;
        Duration aux = Duration.ZERO;
        for (Interval interval : m_interv_array) {
            aux = aux.plus(interval.getDuration());
        }
        m_duration_e = aux;

        if (m_project != null){
            m_project.update(arg);
        }

        Utils.milestone_LOGGER(LOGGER, "info", (String.format("%n %-10s %-10s %-4s %-30s %-30s %-30s %-30s %n",
                "activity: ", m_name, "ID: ", m_id, m_init_DateTime_e.format(dateTimeFormatter),
                m_end_DateTime_e.format(dateTimeFormatter), Utils.format_duration(m_duration_e))), 1);

    }

    public Duration getDuration(){
        return m_duration_e;
    }

    public void addTag(String tag) {
        assert (tag != null): "Null tag provided"; m_tag_array.add(tag);
    }

    Vector<String> getTags() {
        return m_tag_array;
    }

    public String getM_name(){ return m_name; }

    public int getM_id() {return m_id;}

    public LocalDateTime getM_init_DateTime_e(){
        return m_init_DateTime_e;
    }

    public LocalDateTime getM_end_DateTime_e(){
        return m_end_DateTime_e;
    }

    public Boolean getM_active(){ return m_active; }

    public Project getM_project(){
        return m_project;
    }

    public String getM_class(){
        return m_class;
    }

    public Vector<Interval> getM_interv_array(){ return m_interv_array; }


    public JSONObject createJSON(){

        JSONObject m_json_task = new JSONObject();
        m_json_task.put("name", m_name);
        m_json_task.put("id", m_id);
        m_json_task.put("class", m_class);
        m_json_task.put("active", m_active);
        m_json_task.put("init_time", m_init_DateTime_e);
        m_json_task.put("end_time", m_end_DateTime_e);
        m_json_task.put("duration", Utils.format_duration(m_duration_e));

        JSONArray m_json_intervals = new JSONArray(); // Meter al if si se prefiere que no aparezca intervals como []
                                                    // estando vacío
        if (m_interv_array != null) {
            for (Interval interval : m_interv_array) {
                m_json_intervals.put(interval.createJSON());
            }
        }

        m_json_task.put("intervals", m_json_intervals); // También se debe meter al if si se desea que no aparezca vacío

        return m_json_task;
    }

    public JSONObject toJson(int depth){ // Puesto que la máxima depth de una Task es 1 solo hace falta
                                        // invocar a createJSON()
        return this.createJSON();
    }

    @Override
    public Vector<Element> getM_element_array() {
        return null;
    }

    public Vector<Interval> getM_interval_array() {
        return m_interv_array;
    }

    @Override
    public Element findActivityById(int n) {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SearchByTag createTagIterator(String tag) {
        assert (tag != null): "Null tag provided";
        return new SearchByTag(this, tag) {
        };
    }

    public TotalTime createTotalTimeIterator(LocalDateTime init_DateTime, LocalDateTime end_DateTime) {
        assert (init_DateTime != null): "Null tag provided";
        assert (end_DateTime != null): "Null tag provided";
        assert (end_DateTime.isAfter(init_DateTime)): "Initial time provided is posterior to the final time";
        return new TotalTime(this, init_DateTime, end_DateTime) {
        };
    }

    public Boolean containsTag(String tag){
        assert (tag != null): "Null tag provided";
        if (m_tag_array != null) {
            String str= m_tag_array.toString().toLowerCase(); // Así garantizamos que mayúsculas y minúsculas
                                                            // sean indiferentes.
            if (str.contains(tag.toLowerCase())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
