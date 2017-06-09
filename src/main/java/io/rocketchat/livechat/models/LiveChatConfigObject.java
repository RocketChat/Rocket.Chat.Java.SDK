package io.rocketchat.livechat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatConfigObject {

    Boolean enabled;
    String popupTitle;
    String colour;
    Boolean displayRegistrationForm;
    String room;
    JSONArray triggers;
    ArrayList<DepartmentObject> departments;
    Boolean allowSwitchingDepartments;
    Boolean online;
    String offlineColour;
    String offlineMessage;
    String offlineSuccessMessage;
    String offlineUnavailableMessage;
    Boolean displayOfflineFOrm;
    Boolean videoCall;
    String offlineTitle;
    String language;
    Boolean transcript;
    String transcriptMessage;

    public LiveChatConfigObject(JSONObject object) {
        try {
        enabled=object.getBoolean("enabled");
        popupTitle=object.getString("title");
        colour=object.getString("color");
        displayRegistrationForm=object.getBoolean("registrationForm");
        room=object.getString("room");

        // Triggers need to be loaded
        triggers=object.getJSONArray("triggers");

        //Loading departments data
        this.departments=new ArrayList<DepartmentObject>();
        JSONArray departments=object.getJSONArray("departments");
        for (int i=0;i<departments.length();i++){
            this.departments.add(new DepartmentObject(departments.getJSONObject(i)));
        }


        allowSwitchingDepartments=object.getBoolean("allowSwitchingDepartments");
        online=object.getBoolean("online");
        offlineColour=object.getString("offlineColor");
        offlineMessage=object.getString("offlineMessage");
        offlineSuccessMessage=object.getString("offlineSuccessMessage");
        offlineUnavailableMessage=object.getString("offlineUnavailableMessage");
        displayOfflineFOrm=object.getBoolean("displayOfflineForm");
        videoCall=object.getBoolean("videoCall");
        offlineTitle=object.getString("offlineTitle");
        language=object.getString("language");
        transcript=object.getBoolean("transcript");
        transcriptMessage=object.getString("transcriptMessage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getPopupTitle() {
        return popupTitle;
    }

    public void setPopupTitle(String popupTitle) {
        this.popupTitle = popupTitle;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Boolean getDisplayRegistrationForm() {
        return displayRegistrationForm;
    }

    public void setDisplayRegistrationForm(Boolean displayRegistrationForm) {
        this.displayRegistrationForm = displayRegistrationForm;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public JSONArray getTriggers() {
        return triggers;
    }

    public void setTriggers(JSONArray triggers) {
        this.triggers = triggers;
    }

    public ArrayList<DepartmentObject> getDepartments() {
        return departments;
    }

    public void setDepartments(ArrayList<DepartmentObject> departments) {
        this.departments = departments;
    }

    public Boolean getAllowSwitchingDepartments() {
        return allowSwitchingDepartments;
    }

    public void setAllowSwitchingDepartments(Boolean allowSwitchingDepartments) {
        this.allowSwitchingDepartments = allowSwitchingDepartments;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getOfflineColour() {
        return offlineColour;
    }

    public void setOfflineColour(String offlineColour) {
        this.offlineColour = offlineColour;
    }

    public String getOfflineMessage() {
        return offlineMessage;
    }

    public void setOfflineMessage(String offlineMessage) {
        this.offlineMessage = offlineMessage;
    }

    public String getOfflineSuccessMessage() {
        return offlineSuccessMessage;
    }

    public void setOfflineSuccessMessage(String offlineSuccessMessage) {
        this.offlineSuccessMessage = offlineSuccessMessage;
    }

    public String getOfflineUnavailableMessage() {
        return offlineUnavailableMessage;
    }

    public void setOfflineUnavailableMessage(String offlineUnavailableMessage) {
        this.offlineUnavailableMessage = offlineUnavailableMessage;
    }

    public Boolean getDisplayOfflineFOrm() {
        return displayOfflineFOrm;
    }

    public void setDisplayOfflineFOrm(Boolean displayOfflineFOrm) {
        this.displayOfflineFOrm = displayOfflineFOrm;
    }

    public Boolean getVideoCall() {
        return videoCall;
    }

    public void setVideoCall(Boolean videoCall) {
        this.videoCall = videoCall;
    }

    public String getOfflineTitle() {
        return offlineTitle;
    }

    public void setOfflineTitle(String offlineTitle) {
        this.offlineTitle = offlineTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getTranscript() {
        return transcript;
    }

    public void setTranscript(Boolean transcript) {
        this.transcript = transcript;
    }

    public String getTranscriptMessage() {
        return transcriptMessage;
    }

    public void setTranscriptMessage(String transcriptMessage) {
        this.transcriptMessage = transcriptMessage;
    }

    @Override
    public String toString() {
        return "LiveChatConfigObject{" +
                "enabled=" + enabled +
                ", popupTitle='" + popupTitle + '\'' +
                ", colour='" + colour + '\'' +
                ", displayRegistrationForm=" + displayRegistrationForm +
                ", room='" + room + '\'' +
                ", triggers=" + triggers +
                ", departments=" + departments +
                ", allowSwitchingDepartments=" + allowSwitchingDepartments +
                ", online=" + online +
                ", offlineColour='" + offlineColour + '\'' +
                ", offlineMessage='" + offlineMessage + '\'' +
                ", offlineSuccessMessage='" + offlineSuccessMessage + '\'' +
                ", offlineUnavailableMessage='" + offlineUnavailableMessage + '\'' +
                ", displayOfflineFOrm=" + displayOfflineFOrm +
                ", videoCall=" + videoCall +
                ", offlineTitle='" + offlineTitle + '\'' +
                ", language='" + language + '\'' +
                ", transcript=" + transcript +
                ", transcriptMessage='" + transcriptMessage + '\'' +
                '}';
    }
}
