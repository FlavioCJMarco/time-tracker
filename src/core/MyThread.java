package core;

/**
 * Este thread crea el primer objeto clock a partir del Singleton y ejecuta continuamente la función getTime()
 * del clock (observable), invocándola cada dos segundos para actualizar los observers.
 */
public class MyThread extends Thread {
    public void run(){

        ClockTimer clock = ClockTimer.getClockTimer();
        synchronized (this){
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    wait(2000);
                    clock.getTime();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
