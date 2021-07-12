package core;

import java.time.LocalDateTime;
import java.util.Vector;

/**
 * La clase TotalTime implementa el tipo de iterador que se utiliza para el cálculo del tiempo total de las
 * tareas en una determinada franja de tiempo.
 */
public class TotalTime implements Iterator {

    private final Element m_element;
    private final LocalDateTime m_init_DateTime;
    private final LocalDateTime m_end_DateTime;

    public TotalTime(Element element, LocalDateTime init_DateTime, LocalDateTime end_DateTime){
        m_element = element;
        m_init_DateTime = init_DateTime;
        m_end_DateTime = end_DateTime;
    }

    public Element getM_element() {
        return m_element;
    }

    public LocalDateTime getM_init_DateTime(){
        return m_init_DateTime;
    }

    public LocalDateTime getM_end_DateTime() {
        return m_end_DateTime;
    }

    public Vector<Element> getM_element_array(){
        return m_element.getM_element_array();
    }

    /*
    public Element getNext() {
        return null;
    }
    */

    /**
     * La función hasMore() comprueba
     *
     */
    public Boolean hasMore() {

        if (m_element.getM_element_array() != null)
        {
            if (m_element.getM_class().equals("Project")){
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

}
