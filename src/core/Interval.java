package core;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

/**
 * La clase interval representa el nivel más bajo del árbol (project-task-INTERVAL).
 * Los intervalos no están formados por ningún tipo de subelemento inferior y tampoco pueden ser
 * independientes, solo pueden depender de una tarea.
 * A diferencia de Task y Project, Interval NO es un Element.
 *
 * Está definido como Observer.
 *
 * Puede crearse un intervalo a partir de una tarea padre o bien a partir de una tarea padre y un objeto
 * JSON que contenga la información del intervalo.
 *
 * Si se crea un intervalo solo a partir de una tarea padre (sin JSON) se llamará al método startListening()
 * para que observe al Observable ClockTimer y comience a contar el tiempo hasta que se llame al método
 * stopListening() desde la tarea padre.
 *
 * Si se crea un intervalo a partir de una tarea padre y de un objeto JSON se actualizarán las variables
 * del intervalo, pero NO se llamará al método startListening() puesto que se tratará de un intervalo
 * finalizado previamente.
 *
 * También cuenta con setters, getters y métodos para trasladar las variables a formato JSON.
 */
@SuppressWarnings("deprecation")
public class Interval implements Observer{

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private LocalDateTime m_init_DateTime_i;

    public LocalDateTime getM_init_DateTime_i() {
        return m_init_DateTime_i;
    }

    public LocalDateTime getM_end_DateTime_i() {
        return m_end_DateTime_i;
    }

    private LocalDateTime m_end_DateTime_i;
    private Duration m_duration_i;
    private Integer m_id;
    private final String m_class;
    private Boolean m_active;
    private final Task m_tarea;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Interval.class);

    Interval(Task tarea_padre) {
        assert (tarea_padre != null): "Null parent task";
        m_id = IdHandler.getNewId();
        m_duration_i = Duration.ZERO;
        m_tarea = tarea_padre;
        m_class = "Interval";
        startListening();
    }

    Interval(Task tarea_padre, JSONObject json_loader){
        assert (tarea_padre != null): "Null parent task";
        assert (json_loader != null): "No JSON object provided";
        m_tarea = tarea_padre;

        m_id = json_loader.getInt("intervalID");
        m_duration_i = Utils.duration_from_format(json_loader.getString("duration"));
        m_class = json_loader.getString("class");
        m_init_DateTime_i = LocalDateTime.parse(json_loader.getString("init_time"));
        m_end_DateTime_i = LocalDateTime.parse(json_loader.getString("end_time"));
        m_active = json_loader.getBoolean("active");
    }

    public void startListening()
    {
        ClockTimer clock = ClockTimer.getClockTimer();
        m_init_DateTime_i = clock.now();
        m_end_DateTime_i = clock.now();
        m_active = true;

        clock.addObserver(this);
    }


    public void stopListening()
    {
        ClockTimer clock = ClockTimer.getClockTimer();
        m_end_DateTime_i = clock.now();
        m_active = false;
        clock.deleteObserver(this);

        if (m_tarea != null){
            m_tarea.update(m_end_DateTime_i);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void update(Observable o, Object arg) {

        assert (o != null): "Null observable provided";
        assert (arg != null): "Null arg (end date time) provided";

        m_end_DateTime_i=(LocalDateTime)arg;
        m_duration_i = Duration.between(m_init_DateTime_i, (LocalDateTime)arg);
        m_tarea.update((LocalDateTime)arg);

        Utils.milestone_LOGGER(LOGGER, "info", (String.format("%n %-11s %-12s %-30s %-30s %-30s %-30s %n",
                "interval ID: ", m_id, "", m_init_DateTime_i.format(dateTimeFormatter),
                m_end_DateTime_i.format(dateTimeFormatter), Utils.format_duration(m_duration_i))), 1);

    }

    public Duration getDuration(){
        return m_duration_i;
    }

    public void setID(Integer id){
        assert (id != null): "Null interval id provided to set"; m_id = id;
    }

    public Integer getM_ID(){
        return m_id;
    }

    public JSONObject createJSON(){
        JSONObject m_json_interval = new JSONObject();

        m_json_interval.put("active", m_active);
        m_json_interval.put("intervalID", m_id);
        m_json_interval.put("class", m_class);
        m_json_interval.put("init_time", m_init_DateTime_i);
        m_json_interval.put("end_time", m_end_DateTime_i);
        m_json_interval.put("duration", Utils.format_duration(m_duration_i));

        return m_json_interval;
    }


}