package io.rocketchat.core.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 27/7/17.
 */
public class Emoji {
    private String id; //The emoji id
    private String name; //The emoji friendly name
    private JSONArray aliases; //A collection of alias for the emoji. The alias is used to identify the emoji on text and for fast reference from typing - the famous :emoji-alias:. (Each emoji alias is unique per server)
    private String extension; //The emoji file extension
    private Date updatedAt; //The date when the emoji was updated to the server

    public Emoji(JSONObject object) {
        id = object.optString("_id");
        name = object.optString("name");
        aliases = object.optJSONArray("aliases");
        extension = object.optString("extension");
        if (object.opt("_updatedAt") != null) {
            updatedAt = new Date(object.optJSONObject("_updatedAt").optInt("$date"));
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JSONArray getAliases() {
        return aliases;
    }

    public String getExtension() {
        return extension;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", extension='" + extension + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
