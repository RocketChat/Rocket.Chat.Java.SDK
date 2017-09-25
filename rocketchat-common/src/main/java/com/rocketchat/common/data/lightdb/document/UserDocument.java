package com.rocketchat.common.data.lightdb.document;

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

    @Nullable public abstract Boolean active();
    @Nullable public abstract String name();
    // TODO -> private JSONObject services;
    @Nullable public abstract Status status();
    @Nullable public abstract Status statusConnection();
    @Nullable public abstract Status statusDefault();
    @Nullable public abstract Integer utcOffset();

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
    public abstract static class Builder extends BaseUser.Builder<Builder> {
        public abstract Builder active(Boolean active);
        public abstract Builder name(String name);
        public abstract Builder status(Status status);
        public abstract Builder statusConnection(Status status);
        public abstract Builder statusDefault(Status status);
        public abstract Builder utcOffset(Integer offset);

        public abstract UserDocument build();
    }

    public UserDocument update(UserDocument object) {
        UserDocument.Builder builder = toBuilder();

        if (object.username() != null) {
            builder.username(object.username());
        }
        if (object.name() != null) {
            builder.name(object.name());
        }
        if (object.roles() != null) {
            builder.roles(object.roles());
        }
        // TODO - emails
        if (object.active() != null) {
            builder.active(object.active());
        }
        // TODO - services
        if (object.status() != null) {
            builder.status(object.status());
        }
        if (object.statusConnection() != null) {
            builder.statusConnection(object.statusConnection());
        }
        if (object.statusDefault() != null) {
            builder.statusDefault(object.statusDefault());
        }
        if (object.utcOffset() != null) {
            builder.utcOffset(object.utcOffset());
        }

        return builder.build();
    }
}
