package core;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Vector;


/**
 * La clase project representa el nivel más alto del árbol (PROJECT-task-interval).
 * Los proyectos están formados por elementos (tasks u otros proyectos), por lo que pueden ser
 * independientes o depender de un proyecto.
 * Es un Element junto con Task (patrón Composite).
 *
 * Puede crearse un proyecto a partir del nombre (y opcionalmente un proyecto padre) o a partir
 * de un JSONObject (y opcionalmente un proyecto padre).
 *
 * Tiene un vector de Element que almacena todos sub subelementos.
 *
 * También cuenta con setters, getters y métodos para trasladar las variables a formato JSON.
 */
public class Project extends Element {

    protected Vector<Element> m_element_array;
    protected Vector<String> m_tag_array;

    private static final Logger LOGGER = LoggerFactory.getLogger(Project.class);

    public Project(String name) {
        assert (name != null) : "Null name provided";

        m_name = name;
        m_id = IdHandler.getNewId();
        m_duration_e = Duration.ZERO;
        ClockTimer clock = ClockTimer.getClockTimer();
        m_end_DateTime_e = clock.now();
        m_class = "Project";
        m_active = Boolean.FALSE;
        m_tag_array = new Vector<>();
    }

    public Project(String name, Project project_padre) {
        assert (name != null) : "Null name provided";
        assert (project_padre != null) : "Null father project provided";

        ClockTimer clock = ClockTimer.getClockTimer();
        m_name = name;
        m_id = IdHandler.getNewId();
        m_project = project_padre;
        m_duration_e = Duration.ZERO;
        m_end_DateTime_e = clock.now();
        m_class = "Project";
        m_active = Boolean.FALSE;
        m_tag_array = new Vector<>();
    }

    public Project(JSONObject json_loader) {
        assert (json_loader != null) : "Null JSON loader provided";

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

        if (json_loader.has("activities")) {
            JSONArray json_loader_activities = json_loader.getJSONArray("activities");
            JSONObject obj;

            m_element_array = new Vector<>();
            m_tag_array = new Vector<>();

            for (int i = 0; i < json_loader_activities.length(); i++) {
                obj = json_loader_activities.getJSONObject(i);
                if (obj.getString("class").equals("task")) {
                    Task loaded_element = new Task(this, obj);
                    m_element_array.add(loaded_element);
                } else {
                    Project loaded_element = new Project(this, obj);
                    m_element_array.add(loaded_element);
                }
            }
        }
    }

    public Project(Project project_padre, JSONObject json_loader) {
        assert (project_padre != null) : "Null father project provided";
        assert (json_loader != null) : "Null JSON loader provided";

        m_project = project_padre;

        m_name = json_loader.getString("name");
        m_id = json_loader.getInt("id");
        m_duration_e = Utils.duration_from_format(json_loader.getString("duration"));
        m_class = json_loader.getString("class");
        m_active = json_loader.getBoolean("active");

        if (json_loader.has("init_time")) {
            m_init_DateTime_e = LocalDateTime.parse(json_loader.getString("init_time"));
        }

        if (json_loader.has("init_time")) {
            m_end_DateTime_e = LocalDateTime.parse(json_loader.getString("end_time"));
        }

        if (json_loader.has("activities")) {
            JSONArray json_loader_activities = json_loader.getJSONArray("activities");
            JSONObject obj;

            m_element_array = new Vector<>();
            m_tag_array = new Vector<>();

            for (int i = 0; i < json_loader_activities.length(); i++) {
                obj = json_loader_activities.getJSONObject(i);
                if (obj.getString("class").equals("task")) {
                    Task loaded_element = new Task(this, obj);
                    m_element_array.add(loaded_element);
                } else {
                    Project loaded_element = new Project(this, obj);
                    m_element_array.add(loaded_element);
                }
            }
        }
    }


    public Project createProject(String name) {
        assert (name != null) : "Null name provided";

        if (m_element_array == null) {
            ClockTimer clock = ClockTimer.getClockTimer();
            m_element_array = new Vector<>();
            m_init_DateTime_e = clock.now();
        }

        Project elem_aux = new Project(name, this);
        m_element_array.add(elem_aux);
        return elem_aux;

    }

    public Task createTask(String name) {
        assert (name != null) : "Null name provided";

        if (m_element_array == null) {
            ClockTimer clock = ClockTimer.getClockTimer();
            m_element_array = new Vector<>();
            m_init_DateTime_e = clock.now();
        }

        Task elem_aux = new Task(name, this);
        m_element_array.add(elem_aux);
        return elem_aux;

    }

    public void update(LocalDateTime arg) {
        assert (arg != null) : "Null arg (end time) provided for updating";

        m_end_DateTime_e = arg;
        Duration aux = Duration.ZERO;

        m_active = Boolean.FALSE;
        for (Element element : m_element_array) {
            aux = aux.plus(element.getDuration());
            if (element.getM_active()){
                m_active = Boolean.TRUE;
            }
        }
        m_duration_e = aux;

        Utils.milestone_LOGGER(LOGGER, "info", (String.format("%n %-10s %-10s %-4s %-30s %-30s %-30s %-30s %n",
                "activity: ", m_name, "ID: ", m_id, m_init_DateTime_e.format(dateTimeFormatter),
                m_end_DateTime_e.format(dateTimeFormatter), Utils.format_duration(m_duration_e))), 1);

        if (m_project != null) {
            m_project.update(arg);
        }

    }


    public Duration getDuration() {
        return m_duration_e;
    }

    public Vector<Element> getM_element_array() {
        return m_element_array;
    }

    @Override
    public Vector<Interval> getM_interval_array() {
        return null;
    }

    public void addTag(String tag) {
        assert (tag != null) : "Null tag provided";
        m_tag_array.add(tag);
    }

    public Vector<String> getTags() {
        return m_tag_array;
    }

    // --Commented out by Inspection START (03/11/2020 19:29):
    public String getM_name() {
        return m_name;
    }
// --Commented out by Inspection STOP (03/11/2020 19:29)

    public int getM_id() {
        return m_id;
    }

    // --Commented out by Inspection START (03/11/2020 19:29):
    public LocalDateTime getM_init_DateTime_e() {
        return m_init_DateTime_e;
    }
//// --Commented out by Inspection STOP (03/11/2020 19:29)
// --Commented out by Inspection STOP (03/11/2020 19:29)


    public LocalDateTime getM_end_DateTime_e() {
        return m_end_DateTime_e;
    }
// --Commented out by Inspection STOP (03/11/2020 19:29)


    public Boolean getM_active() {
        return m_active;
    }


// --Commented out by Inspection START (03/11/2020 19:40):
//    public Project getM_project(){
//        return m_project;
//    }
// --Commented out by Inspection STOP (03/11/2020 19:40)

    public String getM_class() {
        return m_class;
    }

    // --Commented out by Inspection STOP (03/11/2020 19:40)
    public Project getM_project() {
        return m_project;
    }


    public JSONObject createJSON() {
        JSONObject m_json_project = new JSONObject();
        m_json_project.put("init_time", m_init_DateTime_e);
        m_json_project.put("end_time", m_end_DateTime_e);
        m_json_project.put("duration", m_duration_e);

        if (m_element_array != null) {
            JSONArray m_json_elements = new JSONArray();

            for (Element element : m_element_array) {
                m_json_elements.put(element.createJSON());
            }

            m_json_project.put("activities", m_json_elements);
        }

        m_json_project.put("name", m_name);
        m_json_project.put("id", m_id);
        m_json_project.put("class", m_class);
        m_json_project.put("active", m_active);

        return m_json_project;
    }

    public JSONObject toJson(int depth) {

        JSONObject m_json_project = new JSONObject();
        m_json_project.put("init_time", m_init_DateTime_e);
        m_json_project.put("end_time", m_end_DateTime_e);
        m_json_project.put("duration", Utils.format_duration(m_duration_e));

        if (m_element_array != null) {
            JSONArray m_json_elements = new JSONArray();

            for (Element element : m_element_array) {
                if (depth != 0)
                    m_json_elements.put(element.toJson(depth-1));
                else
                    break;
            }

            m_json_project.put("activities", m_json_elements);
        }

        m_json_project.put("name", m_name);
        m_json_project.put("id", m_id);
        m_json_project.put("class", m_class);
        m_json_project.put("active", m_active);

        return m_json_project;
    }

////////////////////////////////////////////////////////////////////////////////////////////////

    public SearchByTag createTagIterator(String tag) {
        return new SearchByTag(this, tag) {
        };
    }

    public TotalTime createTotalTimeIterator(LocalDateTime init_DateTime, LocalDateTime end_DateTime) {
        assert (init_DateTime != null) : "Null init date time provided";
        assert (end_DateTime != null) : "Null end date time provided";
        assert (end_DateTime.isAfter(init_DateTime)) : "Initial time provided is posterior to the final time.";
        return new TotalTime(this, init_DateTime, end_DateTime) {
        };
    }

    public Boolean containsTag(String tag) {
        assert (tag != null) : "Null tag provided";
        if (m_tag_array != null) {
            String str = m_tag_array.toString().toLowerCase(); // Así garantizamos que mayúsculas y minúsculas
            // sean indiferentes.
            if (str.contains(tag.toLowerCase())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Element findActivityById(int n) {
        assert (n > 0) : "Negative activity ID provided";
        Element subelement = null;

        if (m_id == n) return this;
        else
            if (m_element_array != null) {
                for (Element element : m_element_array) {
                    if (element.getM_id() == n) return element;
                    else if ((element.getM_class().equals("Project")) && (element.getM_element_array() != null))
                        subelement = element.findActivityById(n);
                        if (subelement != null)
                            return subelement;
                }
            }

        return null;
    }
}
