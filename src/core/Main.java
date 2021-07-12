package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Vector;

/**
 * La clase Main incluye el test del programa, la creación del thread principal
 * y las funciones para cargar o almacenar con JSON, llamando a las clases
 * en orden descendentes.
 */
@SuppressWarnings("ALL")
public class Main{

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @SuppressWarnings("unused")
    public static void TestA_tags(){
        Project root = new Project("root");

        Project p0 = root.createProject("software design");
        p0.addTag("java");p0.addTag("flutter");
        //noinspection unused
        Project p1 = root.createProject("software testing");
        p1.addTag("c++");p1.addTag("Java");p1.addTag("python");
        //noinspection unused
        Project p2 = root.createProject("databases");
        p2.addTag("SQL");p2.addTag("python");p2.addTag("C++");
        //noinspection unused
        Project p3 = root.createProject("task transportation");

        Project p4 = p0.createProject("problems");

        Project p5 = p0.createProject("project time tracker");

        Task t1 = p4.createTask("first list");
        t1.addTag("Java");

        Task t2 = p4.createTask("second list");
        t2.addTag("Dart");

        //noinspection unused
        Task t3 = p5.createTask("read handout");
        //noinspection unused
        Task t4 = p5.createTask("first milestone");
        t4.addTag("Java");t4.addTag("IntelliJ");

        Task t0 = root.createTask( "transportation");
        t0.addTag("test");

        String tag_to_search = "JAVA";
        SearchByTag tagIterator = root.createTagIterator(tag_to_search); // SearchByTag is a type of iterator
        Vector<Element> tagResults = Utils.getTagResult(tagIterator);
        Utils.milestone_LOGGER(LOGGER, "info", (String.format("Elementos que contienen la tag %s:", tag_to_search)),
                2);
        for (Element e: tagResults){
            Utils.milestone_LOGGER(LOGGER, "info", e.getM_name(), 2);
        }

    }



    public static void TestB_JSON() throws InterruptedException, IOException {

        Project root = new Project("root");

        Project p0 = root.createProject("software design");

        //noinspection unused
        Project p1 = root.createProject("software testing");

        //noinspection unused
        Project p2 = root.createProject("databases");
        //noinspection unused
        Project p3 = root.createProject("task transportation");

        Project p4 = p0.createProject("problems");

        Project p5 = p0.createProject("project time tracker");

        Task t1 = p4.createTask("first list");

        Task t2 = p4.createTask("second list");

        //noinspection unused
        Task t3 = p5.createTask("read handout");
        //noinspection unused
        Task t4 = p5.createTask("first milestone");

        Task t0 = root.createTask( "transportation");

        Utils.milestone_LOGGER(LOGGER, "info", (String.format("%-30s %-30s %-30s %-30s %-30s %n", "","",
                "initial date", "final date", "duration")), 1);

        Utils.milestone_LOGGER(LOGGER, "warn", "start test", 1);


        t0.createInterval();
        Thread.sleep(4000);
        t0.stopInterval();


        Thread.sleep(2000);

        t1.createInterval();
        Thread.sleep(6000);

        t2.createInterval();
        Thread.sleep(4000);

        t1.stopInterval();

        Thread.sleep(2000);
        t2.stopInterval();

        Thread.sleep(2000);

        t0.createInterval();
        Thread.sleep(4000);
        t0.stopInterval();

        Utils.saveJSON(root);

        Utils.milestone_LOGGER(LOGGER, "warn", "end test", 1);

        // MyLogger.closeHandlers();

    }



    public static void TestC_TotalTime() throws IOException, InterruptedException {

        Project root = new Project("root"); // ID 0

        Project p0 = root.createProject("P0"); // ID 1

        Project p1 = root.createProject("P1"); // ID 2

        Project p3 = root.createProject("P3"); // ID 3

        Task t0 = p0.createTask("T0"); // ID 4
        Task t1 = p0.createTask("T1"); // ID 5
        Task t2 = p0.createTask("T2"); // ID 6

        Task t3 = p1.createTask("T3"); // ID 7

        Task t4 = root.createTask("T4"); // ID 8
        Task t5 = root.createTask("T5"); // ID 9

        Element test = root.findActivityById(7);

        Thread.sleep(10000);

        t0.createInterval();
        Thread.sleep(10000);

        t4.createInterval();
        Thread.sleep(10000);

        t0.stopInterval();
        t4.stopInterval();
        t1.createInterval();
        t2.createInterval();
        Thread.sleep(10000);

        t0.createInterval();
        t5.createInterval();
        Thread.sleep(10000);

        t0.stopInterval();
        t1.stopInterval();
        t4.createInterval();
        Thread.sleep(10000);

        LocalDateTime clock1 = ClockTimer.getClockTimer().now();
        Thread.sleep(10000);

        t5.stopInterval();
        t1.createInterval();
        Thread.sleep(10000);

        t5.createInterval();
        Thread.sleep(10000);

        t5.stopInterval();
        t2.stopInterval();
        Thread.sleep(10000);

        t5.createInterval();
        Thread.sleep(10000);

        t5.stopInterval();
        t2.createInterval();
        Thread.sleep(10000);

        LocalDateTime clock2 = ClockTimer.getClockTimer().now();
        Thread.sleep(10000);

        t1.stopInterval();
        t2.stopInterval();
        t3.createInterval();
        t4.stopInterval();
        Thread.sleep(10000);

        t0.createInterval();
        t3.stopInterval();
        t4.createInterval();
        Thread.sleep(10000);

        t0.stopInterval();
        Thread.sleep(10000);

        t4.stopInterval();

        TotalTime timeIterator = root.createTotalTimeIterator(clock1, clock2);
        Duration timeResults = Utils.TotalTime_calculation(timeIterator);
        Utils.milestone_LOGGER(LOGGER, "info", String.format(
                "Tiempo total (root project): %s", Utils.format_duration(timeResults)), 2);

        Utils.logTotalTimeTree(LOGGER, timeIterator);
    }




    public static void Test_Load_JSON() throws IOException {
        // El booleano indica si se desea que el contenido del árbol leído se muestre por pantalla.
        //noinspection unused
        Project root = (Project) Utils.loadJSON("output.json", Boolean.FALSE);

        Utils.milestone_LOGGER(LOGGER, "warn", "Se ha cargado un árbol a partir de un fichero JSON.", 1);
        Utils.milestone_LOGGER(LOGGER, "debug", "Este log se puede utilizar como breakpoint para comprobar" +
                "la variable root y ver la estructura en árbol. NOTA: m_element_array contiene todos los hijos" +
                "de un elemento.", 1);
    }


    /**
     * Llamadas a los tests.
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        Utils.create_thread();

        // TestA_tags();

        //TestB_JSON();
        //Test_Load_JSON();

        TestC_TotalTime();
    }
}
