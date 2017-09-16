package com.rocketchat.core.db.Document;

import com.rocketchat.core.model.FileObject;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 16/9/17.
 */
public class FileDocument extends FileObject {

    public FileDocument(JSONObject object) {
        super(object);
    }

    public void update(JSONObject file) {
        try {
            if (file.opt("_id") != null) {
                fileId = file.getString("_id");
            }

            if (file.opt("name") != null) {
                fileName = file.getString("name");
            }

            if (file.opt("description") != null) {
                description = file.getString("description");
            }

            if (file.opt("size") != null) {
                size = file.getInt("size");
            }
            if (file.opt("type") != null) {
                fileType = file.getString("type");
            }

            if (file.opt("rid") != null) {
                roomId = file.getString("rid");
            }

            if (file.opt("extension") != null) {
                extension = file.getString("extension");
            }

            if (file.opt("store") != null) {
                store = file.getString("store");
            }

            if (file.opt("_updatedAt") != null) {
                updatedAt = new Date(file.getJSONObject("_updatedAt").getLong("$date"));
            }

            if (file.opt("uploadedAt") != null) {
                uploadedAt = new Date(file.getJSONObject("uploadedAt").getLong("$date"));
            }

            if (file.opt("url") != null) {
                url = file.getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
