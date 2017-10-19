package com.rocketchat.common.data.lightstream.document;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.model.BaseUser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Created by sachin on 13/8/17.
 */
@AutoValue
public abstract class UserDocument extends BaseUser {

    @Nullable
    public abstract Boolean active();

    @Nullable
    public abstract String name();

    // TODO -> private JSONObject services (Add services JSONObject inside model);
    @Nullable
    public abstract Status status();

    @Nullable
    public abstract Status statusConnection();

    @Nullable
    public abstract Status statusDefault();

    @Nullable
    public abstract Double utcOffset();

    public static JsonAdapter<UserDocument> jsonAdapter(Moshi moshi) {
        return new AutoValue_UserDocument.MoshiJsonAdapter(moshi);
    }

    public static Builder builder() {
        return new AutoValue_UserDocument.Builder();
    }

    public abstract Builder toBuilder();

    public UserDocument withId(String id) {
        return toBuilder().id(id).build();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseBuilder<Builder> {
        public abstract Builder active(Boolean active);

        public abstract Builder name(String name);

        public abstract Builder status(Status status);

        public abstract Builder statusConnection(Status status);

        public abstract Builder statusDefault(Status status);

        public abstract Builder utcOffset(Double offset);

        public abstract UserDocument build();
    }
}
