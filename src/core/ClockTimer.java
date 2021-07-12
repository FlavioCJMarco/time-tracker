package core;

import java.time.LocalDateTime;
import java.util.Observable;

/**
 * La clase ClockTimer es un elemento observable responsable de todos los elementos relacionados
 * con el tiempo en el programa y de proporcionar el tiempo actual cuando sea necesario,
 * de forma que no existan inconsistencias temporales.
 */
@SuppressWarnings("deprecation")
public class ClockTimer extends Observable{
    LocalDateTime date = null;
    private static ClockTimer clockTimer;

    public static void main(String[] args){
        System.out.println("hola");
        //tick();
    }

    public static ClockTimer getClockTimer(){ //Singleton
            if (clockTimer == null) {
                clockTimer = new ClockTimer();
            }
            return clockTimer;
        }

    public void getTime(){
        synchronized (this) {
            date = LocalDateTime.now();
            //set change
            setChanged();
            //notify observers for change
            notifyObservers(date);
        }
    }

    public LocalDateTime now(){
        return LocalDateTime.now();
    }


}