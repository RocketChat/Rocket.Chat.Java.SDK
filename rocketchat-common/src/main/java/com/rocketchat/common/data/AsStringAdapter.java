package com.rocketchat.common.data;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;

import java.io.IOException;

public class AsStringAdapter {
    @FromJson
    @AsString
    public String fromJsonValue(JsonReader reader) throws IOException {
        JsonReader.Token token = reader.peek();
        String value = null;
        switch (token) {
            case STRING:
                value = reader.nextString();
                break;
            case BOOLEAN:
                value = String.valueOf(reader.nextBoolean());
                break;
            case NUMBER:
                value = String.valueOf(reader.nextLong());
                break;
            case BEGIN_OBJECT:
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (reader.peek() == JsonReader.Token.STRING) {
                        value = reader.nextString();
                    } else {
                        // Skipping non string value.
                        reader.skipValue();
                        throw new IOException("Invalid value: " + name);
                    }
                }
                reader.endObject();
        }
        return value;
    }

    @ToJson
    public void toString(JsonWriter writer, @AsString String value) throws IOException {
        throw new IOException("NOT IMPLEMENTED");
    }
}
