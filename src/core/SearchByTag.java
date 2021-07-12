package core;

import java.util.Vector;

/**
 * La clase SearchByTag implementa el tipo de iterador que se utiliza para la búsqueda por etiquetas de los
 * elementos en una determinada franja de tiempo.
 */
public class SearchByTag implements Iterator {

    private final Element m_element;
    private final String m_tag;

    public SearchByTag(Element element, String tag){
       m_element = element;
       m_tag = tag;
    }

    public String getTag(){
        return m_tag;
    }

    public Element getM_element(){
        return m_element;
    }

    public Vector<Element> getM_element_array(){
        return m_element.getM_element_array();
    }

    /*
    public Element getNext() {
        return null;
    }
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
