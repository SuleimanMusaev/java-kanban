package taskapp.managerswithtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasksapp.manager.FileBackedTaskManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTestWithTime<FileBackedTaskManager> {

    private File tempFile;

    @BeforeEach
    void init() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,type,name,status,description,epic,start,duration\n");
        }
    }

    @Test
    public void shouldThrowExceptionIfFileIsMissing() {
        File missingFile = new File("missing_file.csv");
        assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager.loadFromFile(missingFile);
        }, "Исключение при отсутствии файла");
    }

    @Test
    public void shouldNotThrowOnValidFile() {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(tempFile);
        });
    }
}
