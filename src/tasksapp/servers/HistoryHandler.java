package tasksapp.servers;

import com.sun.net.httpserver.HttpExchange;
import tasksapp.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, gson.toJson(manager.getHistory()), 200);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
