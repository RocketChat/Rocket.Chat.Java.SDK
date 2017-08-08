package io.rocketchat.core.model;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 26/7/17.
 */

public class PublicSetting {

    private String id;
    private String type;
    private Boolean ispublic;
    private String section;
    private JSONObject enableQuery;
    private String group;
    private Boolean hidden;
    private Object packageValue;   //Can be Boolean or String
    private String valueSource;
    private Boolean blocked;
    private Integer sorter;
    private String i18nLabel;
    private String i18nDescription;
    private Date timestamp;
    private Date updatedAt;
    private Date createdAt;
    private Object value;          // Can be Boolean or String
    private String meteorSettingsValue;
    private MetaData metaData;
    private Integer loki;

    public PublicSetting(JSONObject object) {
        id = object.optString("_id");
        type = object.optString("type");
        ispublic = object.optBoolean("public");
        section = object.optString("section");
        enableQuery = object.optJSONObject("enableQuery");
        group = object.optString("group");
        hidden = object.optBoolean("hidden");
        packageValue = object.opt("packageValue");
        valueSource = object.optString("valueSource");
        blocked = object.optBoolean("blocked");
        sorter = object.optInt("sorter");
        i18nLabel = object.optString("i18nLabel");
        i18nDescription = object.optString("i18nDescription");
        if (object.opt("ts") != null) {
            timestamp = new Date(object.optJSONObject("ts").optInt("$date"));
        }
        if (object.opt("_updatedAt") != null) {
            updatedAt = new Date(object.optJSONObject("_updatedAt").optInt("$date"));
        }
        if (object.opt("createdAt") != null) {
            createdAt = new Date(object.optJSONObject("createdAt").optInt("$date"));
        }
        value = object.opt("value");
        meteorSettingsValue = object.optString("meteorSettingsValue");
        metaData = new MetaData(object.optJSONObject("meta"));
        loki = object.optInt("$loki");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Boolean getIspublic() {
        return ispublic;
    }

    public String getSection() {
        return section;
    }

    public JSONObject getEnableQuery() {
        return enableQuery;
    }

    public String getGroup() {
        return group;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public Object getPackageValue() {
        return packageValue;
    }

    public String getValueSource() {
        return valueSource;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public Integer getSorter() {
        return sorter;
    }

    public String getI18nLabel() {
        return i18nLabel;
    }

    public String getI18nDescription() {
        return i18nDescription;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Object getValue() {
        return value;
    }

    public String getMeteorSettingsValue() {
        return meteorSettingsValue;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Integer getLoki() {
        return loki;
    }
}
