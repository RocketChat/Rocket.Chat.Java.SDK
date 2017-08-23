package com.rocketchat.core.model.attachment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 20/8/17.
 */

public class Attachment {
    protected String title;
    protected String type;
    protected String description;
    protected String title_link;
    protected Boolean title_link_download;

    Attachment(JSONObject object) {
        title = object.optString("title");
        type = object.optString("type");
        description = object.optString("description");
        title_link = object.optString("title_link");
        title_link_download = object.optBoolean("title_link_download");
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle_link() {
        return title_link;
    }

    public Boolean getTitle_link_download() {
        return title_link_download;
    }


    public static class TextAttachment implements TAttachment {
        private String text;
        private JSONObject translations;
        private String author_name;
        private String author_icon;
        private String message_link;
        private List<Attachment> attachments; //A collection of attachment objects, available only when the message has at least one attachment
        private Date msgTimestamp;

        public TextAttachment(JSONObject object) {
            try {
                text = object.optString("text");
                translations = object.optJSONObject("translations");
                author_name = object.optString("author_name");
                author_icon = object.optString("author_icon");
                message_link = object.optString("message_link");

                if (object.opt("attachments") != null) {
                    attachments = new ArrayList<>();
                    JSONArray array = object.optJSONArray("attachments");
                    for (int i = 0; i < array.length(); i++) {
                        attachments.add(new Attachment(array.optJSONObject(i)));
                    }
                }

                if (object.optJSONObject("ts") != null) {
                    msgTimestamp = new Date(object.getJSONObject("ts").getLong("$date"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getText() {
            return text;
        }

        public JSONObject getTranslations() {
            return translations;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public String getAuthor_icon() {
            return author_icon;
        }

        public String getMessage_link() {
            return message_link;
        }

        public List<Attachment> getAttachments() {
            return attachments;
        }

        public Date getMsgTimestamp() {
            return msgTimestamp;
        }

        @Override
        public Type getAttachmentType() {
            return Type.TEXT_ATTACHMENT;
        }
    }

    public static class ImageAttachment extends Attachment implements TAttachment {
        // Exclusively for image
        String image_url;
        String image_type;
        int image_size;

        public ImageAttachment(JSONObject object) {
            super(object);
            // Exclusively for image
            image_url = object.optString("image_url");
            image_type = object.optString("image_type");
            image_size = object.optInt("image_size");
        }

        public String getImage_url() {
            return image_url;
        }

        public String getImage_type() {
            return image_type;
        }

        public int getImage_size() {
            return image_size;
        }

        @Override
        public Type getAttachmentType() {
            return Type.IMAGE;
        }
    }

    public static class AudioAttachment extends Attachment implements TAttachment {

        // Exclusively For audio
        String audio_url;
        String audio_type;
        int audio_size;

        public AudioAttachment(JSONObject object) {
            super(object);
            // Exclusively For audio
            audio_url = object.optString("audio_url");
            audio_type = object.optString("audio_type");
            audio_size = object.optInt("audio_size");

        }

        public String getAudio_url() {
            return audio_url;
        }

        public String getAudio_type() {
            return audio_type;
        }

        public int getAudio_size() {
            return audio_size;
        }

        @Override
        public Type getAttachmentType() {
            return Type.AUDIO;
        }
    }

    public static class VideoAttachment extends Attachment implements TAttachment {

        // Exclusively For video
        String video_url;
        String video_type;
        int video_size;

        public VideoAttachment(JSONObject object) {
            super(object);
            // Exclusively For video
            video_url = object.optString("video_url");
            video_type = object.optString("video_type");
            video_size = object.optInt("video_type");
        }

        public String getVideo_url() {
            return video_url;
        }

        public String getVideo_type() {
            return video_type;
        }

        public int getVideo_size() {
            return video_size;
        }

        @Override
        public Type getAttachmentType() {
            return Type.VIDEO;
        }
    }

    public enum Type {
        TEXT_ATTACHMENT,
        IMAGE,
        AUDIO,
        VIDEO,
        OTHER
    }
}

