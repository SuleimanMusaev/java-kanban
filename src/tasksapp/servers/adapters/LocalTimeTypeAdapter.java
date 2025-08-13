package tasksapp.servers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeTypeAdapter extends TypeAdapter<LocalTime> {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalTime localTime) throws IOException {
        if (localTime != null) {
            jsonWriter.value(localTime.format(timeFormatter));
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public LocalTime read(JsonReader jsonReader) throws IOException {
        String text = jsonReader.nextString();
        if (text != null) {
            return LocalTime.parse(text, timeFormatter);
        }
        return null;
    }
}
