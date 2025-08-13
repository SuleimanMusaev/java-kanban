package tasksapp.servers;

import com.sun.net.httpserver.HttpExchange;
import tasksapp.manager.TaskManager;
import tasksapp.model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
                    Epic epic = manager.getEpicById(id);
                    if (epic != null) {
                        sendText(exchange, gson.toJson(epic), 200);
                    } else {
                        sendNotFound(exchange);
                    }
                } else {
                    sendText(exchange, gson.toJson(manager.getAllEpics()), 200);
                }
            } else if ("POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic.getId() == 0) {
                    manager.createEpic(epic);
                } else {
                    manager.updateEpic(epic);
                }
                sendText(exchange, gson.toJson(epic), 201);
            } else if ("DELETE".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.substring(3));
                    manager.deleteEpic(id);
                } else {
                    manager.deleteAllEpics();
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
