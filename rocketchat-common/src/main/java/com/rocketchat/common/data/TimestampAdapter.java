package com.rocketchat.common.data;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

public class TimestampAdapter {
    private final ISO8601Converter dateConverter;
    private final JsonReader.Options options = JsonReader.Options.of("$date");

    public TimestampAdapter(ISO8601Converter dateConverter) {
        this.dateConverter = dateConverter;
    }

    @FromJson
    @ISO8601Date
    public Long fromTimestampObject(JsonReader reader) throws IOException {
        Long timestamp = null;
        String result = null;
        JsonReader.Token token = reader.peek();
        if (token == JsonReader.Token.BEGIN_OBJECT) {
            reader.beginObject();
            if (reader.hasNext()) {
                switch (reader.selectName(options)) {
                    case 0: {
                        if (reader.peek() == JsonReader.Token.NULL) {
                            timestamp = reader.nextNull();
                        } else {
                            timestamp = reader.nextLong();
                        }
                    }
                }
            }
            reader.endObject();
        } else if (token == JsonReader.Token.STRING) {
            if (reader.peek() != JsonReader.Token.NULL) {
                result = reader.nextString();
                try {
                    timestamp = dateConverter.toTimestamp(result);
                } catch (ParseException e) {
                    throw new IOException("Error parsing date: " + result, e);
                }
            }
        }
        return timestamp;
    }

    @ToJson
    public void toTimestampObject(JsonWriter writer, @ISO8601Date Long value) throws IOException {
        throw new IOException("NOT IMPLEMENTED");
        /*if (value != null && value != 0) {
            writer.beginObject().name("$date").value(value).endObject();
        }*/
        //writer.value(value);
    }
}
