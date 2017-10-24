package com.rocketchat.common.data;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;
import java.io.IOException;

public class TimestampAdapter {
    JsonReader.Options options = JsonReader.Options.of("$date");

    @FromJson
    @Timestamp
    public Long fromTimestampObject(JsonReader reader) throws IOException {
        Long result = null;
        reader.beginObject();
        if (reader.hasNext()) {
            switch (reader.selectName(options)) {
                case 0:
                    result = reader.nextLong();
            }
        }
        reader.endObject();
        return result;
    }


    @ToJson
    public void toTimestampObject(JsonWriter writer, @Timestamp Long value) throws IOException {
        if (value != null) {
            writer.beginObject().name("$date").value(value).endObject();
        }
    }
}
