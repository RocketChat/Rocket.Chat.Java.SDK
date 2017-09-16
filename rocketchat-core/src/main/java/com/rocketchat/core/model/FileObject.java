package com.rocketchat.core.model;

import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 18/8/17.
 */
public class FileObject {

    protected String fileId;
    protected String fileName;
    protected String description;
    protected int size;
    protected String fileType;
    protected String roomId;
    protected String extension;
    protected String store;

    protected Date uploadedAt;
    protected Date updatedAt;
    protected String url;

    public FileObject(JSONObject object) {

        try {
            fileId = object.optString("_id");
            fileName = object.optString("name");
            description = object.optString("description");
            size = object.optInt("size");
            fileType = object.optString("type");
            roomId = object.optString("rid");
            extension = object.optString("extension");
            store = object.optString("store");

            if (object.opt("_updatedAt") != null) {
                updatedAt = new Date(object.getJSONObject("_updatedAt").getLong("$date"));
            }
            if (object.opt("uploadedAt") != null) {
                uploadedAt = new Date(object.getJSONObject("uploadedAt").getLong("$date"));
            }
            url = object.optString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }

    public String getFileType() {
        return fileType;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getExtension() {
        return extension;
    }

    public String getStore() {
        return store;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "FileObject{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", description='" + description + '\'' +
                ", size=" + size +
                ", fileType='" + fileType + '\'' +
                ", roomId='" + roomId + '\'' +
                ", extension='" + extension + '\'' +
                ", store='" + store + '\'' +
                ", uploadedAt=" + uploadedAt +
                ", updatedAt=" + updatedAt +
                ", url='" + url + '\'' +
                '}';
    }
}
