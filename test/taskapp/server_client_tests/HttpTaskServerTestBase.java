package taskapp.server_client_tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasksapp.manager.HistoryManager;
import tasksapp.manager.InMemoryHistoryManager;
import tasksapp.manager.InMemoryTaskManager;
import tasksapp.manager.TaskManager;

import java.io.IOException;

public class HttpTaskServerTestBase {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
        server = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }
}
