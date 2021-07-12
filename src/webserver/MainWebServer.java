package webserver;

import core.Element;
import core.Project;
import core.Task;
import core.Utils;

public class MainWebServer {
    public static void main(String[] args) {
        webServer();
    }

    public static void webServer() {
        final Element root = makeTreeCourses();
        // implement this method that returns the tree of
        // appendix A in the practicum handout

        Utils.create_thread();
        // start your clock

        new WebServer(root);
    }

    public static Element makeTreeCourses() {
        Project root = new Project("root");

        Project p0 = root.createProject("software design");
        p0.addTag("java");
        p0.addTag("flutter");
        //noinspection unused
        Project p1 = root.createProject("software testing");
        p1.addTag("c++");
        p1.addTag("Java");
        p1.addTag("python");
        //noinspection unused
        Project p2 = root.createProject("databases");
        p2.addTag("SQL");
        p2.addTag("python");
        p2.addTag("C++");
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
        t4.addTag("Java");
        t4.addTag("IntelliJ");

        Task t0 = root.createTask("transportation");
        t0.addTag("test");

        return root;
    }
}