package com.rocketchat.core.internal.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.model.internal.TypedListResponse;
import com.rocketchat.common.data.model.internal.TypedResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

@AutoValue
public abstract class RestResult<T> {
    public abstract T result();

    public static class MoshiJsonAdapter<T> extends JsonAdapter<RestResult<T>> {
        private static final String[] NAMES = new String[] {"status", "success"};
        private static final JsonReader.Options OPTIONS = JsonReader.Options.of(NAMES);
        private final JsonAdapter<T> tAdaptper;

        public MoshiJsonAdapter(Moshi moshi, Type[] types) {
            this.tAdaptper = adapter(moshi, types[0]);
        }
        @Nullable
        @Override
        public RestResult<T> fromJson(JsonReader reader) throws IOException {
            reader.beginObject();
            T result = null;
            while (reader.hasNext()) {
                switch (reader.selectName(OPTIONS)) {
                    case 0:
                    case 1: {
                        // Just ignore status or success value, since this is for parsing 200 OK messages
                        reader.skipValue();
                        break;
                    }
                    case -1: {
                        reader.nextName();
                        JsonReader.Token token = reader.peek();
                        if (token == JsonReader.Token.BEGIN_ARRAY || token == JsonReader.Token.BEGIN_OBJECT) {
                            result = this.tAdaptper.fromJson(reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                }
            }
            reader.endObject();
            return new AutoValue_RestResult(result);
        }

        @Override
        public void toJson(JsonWriter writer, @Nullable RestResult<T> value) throws IOException {

        }

        private JsonAdapter adapter(Moshi moshi, Type adapterType) {
            return moshi.adapter(adapterType);
        }
    }

    public static class JsonAdapterFactory implements JsonAdapter.Factory {
        @Nullable
        @Override
        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            if (!annotations.isEmpty()) return null;
            if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType.equals(RestResult.class)) {
                    return new RestResult.MoshiJsonAdapter(moshi, ((ParameterizedType) type).getActualTypeArguments());
                }
            }
            return null;
        }
    }
}
