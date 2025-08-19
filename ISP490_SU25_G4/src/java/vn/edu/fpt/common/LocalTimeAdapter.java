package vn.edu.fpt.common;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    // Sử dụng định dạng chuẩn ISO cho giờ, ví dụ: "14:30:55"
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public JsonElement serialize(LocalTime time, Type typeOfSrc, JsonSerializationContext context) {
        if (time == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(time.format(formatter));
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull() || json.getAsString().isEmpty()) {
            return null;
        }
        return LocalTime.parse(json.getAsString(), formatter);
    }
}