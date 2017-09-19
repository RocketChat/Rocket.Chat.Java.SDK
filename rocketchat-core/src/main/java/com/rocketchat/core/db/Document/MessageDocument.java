package com.rocketchat.core.db.Document;

import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.MessageUrl;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.attachment.Attachment;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 16/9/17.
 */
public class MessageDocument extends RocketChatMessage {
    public MessageDocument(JSONObject object) {
        super(object);
    }

    public void update(JSONObject object) {

        try {
            if (object.opt("_id") != null) {
                messageId = object.getString("_id");
            }
            if (object.opt("rid") != null) {
                roomId = object.getString("rid");
            }
            if (object.opt("msg") != null) {
                message = object.getString("msg");
            }
            if (object.opt("ts") != null) {
                msgTimestamp = new Date(object.getJSONObject("ts").getLong("$date"));
            }
            if (object.opt("u") != null) {
                sender = new UserObject(object.getJSONObject("u"));
            }
            if (object.opt("_updatedAt") != null) {
                updatedAt = new Date(object.getJSONObject("_updatedAt").getLong("$date"));
            }
            if (object.opt("editedAt") != null) {
                editedAt = new Date(object.getJSONObject("editedAt").getLong("$date"));
                editedBy = new UserObject(object.getJSONObject("editedBy"));
            }
            if (object.opt("t") != null) {
                messagetype = object.getString("t");
            }
            if (object.opt("alias") != null) {
                senderAlias = object.getString("alias");
            }
            if (object.opt("mentions") != null) {
                mentions = object.optJSONArray("mentions");
            }
            if (object.opt("channels") != null) {
                channels = object.optJSONArray("channels");
            }
            if (object.opt("groupable") != null) {
                groupable = object.optBoolean("groupable");
            }
            if (object.opt("file") != null) {
                file = new FileObject(object.optJSONObject("file"));
            }
            if (object.opt("urls") != null) {
                urls = new ArrayList<>();
                JSONArray array = object.optJSONArray("urls");
                for (int i = 0; i < array.length(); i++) {
                    urls.add(new MessageUrl(array.optJSONObject(i)));
                }
            }
            if (object.opt("attachments") != null) {
                attachments = new ArrayList<>();
                JSONArray array = object.optJSONArray("attachments");
                for (int i = 0; i < array.length(); i++) {
                    if (file == null) {
                        attachments.add(new Attachment.TextAttachment(array.optJSONObject(i)));
                    } else {
                        String type = file.getFileType();
                        if (type.contains("image")) {
                            attachments.add(new Attachment.ImageAttachment(array.optJSONObject(i)));
                        } else if (type.contains("video")) {
                            attachments.add(new Attachment.VideoAttachment(array.optJSONObject(i)));
                        } else if (type.contains("audio")) {
                            attachments.add(new Attachment.AudioAttachment(array.optJSONObject(i)));
                        }
                    }
                }
            }
            if (object.opt("avatar") != null) {
                avatar = object.optString("avatar");
            }
            if (object.opt("parseUrls") != null) {
                parseUrls = object.optBoolean("parseUrls");
            }

            if (object.opt("translations") != null) {
                translations = object.optJSONObject("translations");
            }

            if (object.opt("starred") != null) {
                starred_by = new ArrayList<>();
                JSONArray array = object.optJSONArray("starred");
                for (int i = 0; i < array.length(); i++) {
                    starred_by.add(array.optJSONObject(i).optString("_id"));
                }
            }
            if (object.opt("reactions") != null) {
                reactions = object.optJSONObject("reactions");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
