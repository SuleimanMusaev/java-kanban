package taskapp.server_client_tests;

import org.junit.jupiter.api.Test;
import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerSubtasksTest extends HttpTaskServerTestBase {
    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Desc", TaskStatus.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(Duration.ofMinutes(45));
        String json = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Subtask 1", subtasks.get(0).getName());
    }
}
