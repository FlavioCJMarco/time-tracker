package core;

/**
 * La interfaz Iterator implementa los métodos de los que se sirven los iteradores para avanzar o detener el recorrido
 * del árbol. La función getNext() finalmente no se implementó debido al caracter recursivo de las funciones que
 * se valen de los dos tipos de iteradores existentes (getTagResult() y TotalTime(), en utils.java).
 */
public interface Iterator {
    // Element getNext();
    Boolean hasMore();
}
