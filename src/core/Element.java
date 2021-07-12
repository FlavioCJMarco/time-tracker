package core;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;


/**
 * La clase abstracta Element incluye todos los atributos de los elementos Project y Task, así como los métodos
 * abstractos comunes a ambos.
 */
@SuppressWarnings("ALL")
public abstract class Element implements IterableCollection {
    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    protected static final Logger LOGGER = LoggerFactory.getLogger(Element.class);

    protected Vector<String> m_tag_array;

    protected String m_name;
    protected int m_id;
    protected LocalDateTime m_init_DateTime_e;
    protected LocalDateTime m_end_DateTime_e;
    protected Duration m_duration_e;
    @SuppressWarnings("unused")
    protected Boolean m_active;
    protected Project m_project;
    protected String m_class;

    abstract Duration getDuration();

    abstract void addTag(String tag);
    abstract Vector<String> getTags();

    abstract LocalDateTime getM_init_DateTime_e();

    abstract LocalDateTime getM_end_DateTime_e();

    abstract Boolean getM_active();


    abstract Project getM_project();
    abstract String getM_name();
    abstract int getM_id();

    abstract String getM_class();

    abstract JSONObject createJSON();
    public abstract  JSONObject toJson(int depth); // Igual que la función anterior, pero con una depth asociada.
    abstract Boolean containsTag(String tag);

    public abstract Vector<Element> getM_element_array();
    public abstract Vector<Interval> getM_interval_array();

    public abstract Element findActivityById(int n);
}




