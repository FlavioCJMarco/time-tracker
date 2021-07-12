package core;

public class IdHandler {

    private static int id = -1;

    public static int getNewId(){ //Singleton
        id = id + 1;
        return id;
    }

}
