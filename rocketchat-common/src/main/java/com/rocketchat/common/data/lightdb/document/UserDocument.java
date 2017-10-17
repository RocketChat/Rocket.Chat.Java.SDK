package com.rocketchat.common.data.lightdb.document;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.model.BaseUser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import javax.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public UserDocument update(JSONObject object) {
        UserDocument.Builder builder = toBuilder();

        if (object.opt("username") != null) {
            builder.username(object.optString("username"));
        }
        if (object.opt("name") != null) {
            builder.name(object.optString("name"));
        }
        if (object.opt("roles") != null) {
            ArrayList<String> roles = new ArrayList<>();
            JSONArray array = object.optJSONArray("roles");
            for (int i = 0; i < array.length(); i++) {
                roles.add(array.optString(i));
            }
            builder.roles(roles);
        }
        // TODO - emails
        if (object.opt("active") != null) {
            builder.active(object.optBoolean("active"));
        }
        // TODO - services
        if (object.opt("status") != null) {
            builder.status(BaseUser.getStatus(object.optString("status")));
        }
        if (object.opt("statusConnection") != null) {
            builder.statusConnection(BaseUser.getStatus(object.optString("statusConnection")));
        }

        if (object.opt("statusDefault") != null) {
            builder.statusDefault(BaseUser.getStatus(object.optString("statusDefault")));
        }
        if (object.opt("utcOffset") != null) {
            builder.utcOffset(object.optDouble("utcOffset"));
        }

        return builder.build();
    }
}
