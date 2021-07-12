package core;

import java.time.LocalDateTime;

/**
 * La interfaz IterableCollection implementa los métodos que retornan un iterador SearchByTag o un iterador TotalTime,
 * que se utilizarán en Project o Task.
 */
public interface IterableCollection {
    SearchByTag createTagIterator(String tag);
    TotalTime createTotalTimeIterator(LocalDateTime init_DateTime, LocalDateTime end_DateTime);
}
