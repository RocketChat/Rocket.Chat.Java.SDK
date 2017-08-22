package com.rocketchat.core.uploader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 18/8/17.
 */
public class FileUploadToken {
    private String fileId;
    private String token;
    private String url;

    public FileUploadToken(JSONObject object) {
        try {
            fileId = object.getString("fileId");
            token = object.getString("token");
            url = object.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFileId() {
        return fileId;
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "FileUploadToken{" +
                "fileId='" + fileId + '\'' +
                ", token='" + token + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
