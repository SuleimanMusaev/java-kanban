package tasksapp.servers;

import com.sun.net.httpserver.HttpExchange;
import tasksapp.manager.TaskManager;
import tasksapp.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            if ("GET".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    Subtask subtask = manager.getSubtaskById(id);
                    if (subtask != null) {
                        sendText(exchange, gson.toJson(subtask), 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } else {
                    sendText(exchange, gson.toJson(manager.getAllSubtasks()), 200);
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);

                if (subtask == null) {
                    sendServerError(exchange, "Ошибка парсинга JSON. Проверьте формат и поля.");
                    return;
                }

                manager.createSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 201);
            } else if ("DELETE".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    manager.deleteSubtask(id);
                } else {
                    manager.deleteAllSubtasks();
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
