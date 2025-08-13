package tasksapp.app;

import com.sun.net.httpserver.HttpServer;
import tasksapp.manager.Managers;
import tasksapp.manager.TaskManager;
import tasksapp.servers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServerApp {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServerApp(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        new HttpTaskServerApp(manager).start();
    }
}
