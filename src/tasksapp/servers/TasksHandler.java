package tasksapp.servers;

import com.sun.net.httpserver.HttpExchange;
import tasksapp.manager.TaskManager;
import tasksapp.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if ("GET".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    Task task = manager.getTaskById(id);
                    if (task != null) {
                        sendText(exchange, gson.toJson(task), 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } else {
                    sendText(exchange, gson.toJson(manager.getAllTasks()), 200);
                }
            } else if ("POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                if (task.getId() == 0) {
                    manager.createTask(task);
                    sendText(exchange, gson.toJson(task), 201);
                } else {
                    manager.updateTask(task);
                    sendText(exchange, gson.toJson(task), 201);
                }
            } else if ("DELETE".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    manager.deleteTask(id);
                } else {
                    manager.deleteAllTasks();
                }
                sendText(exchange, "{}", 200);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}