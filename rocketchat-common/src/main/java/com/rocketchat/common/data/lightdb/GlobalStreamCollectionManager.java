package com.rocketchat.common.data.lightdb;

import com.rocketchat.common.data.lightdb.document.ClientVersionsDocument;
import com.rocketchat.common.data.lightdb.document.LoginConfDocument;
import com.rocketchat.common.data.lightdb.document.RocketChatRolesDocument;
import com.rocketchat.common.data.lightdb.document.UserDocument;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.listener.StreamCollectionListener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 11/8/17.
 */
public class GlobalStreamCollectionManager {

    private ConcurrentLinkedQueue<StreamCollectionListener<UserDocument>> usersCollectionListener;
    private ConcurrentLinkedQueue<StreamCollectionListener<LoginConfDocument>> loginConfDocumentCollectionListener;
    private ConcurrentLinkedQueue<StreamCollectionListener<RocketChatRolesDocument>> rolesDocumentCollectionListener;
    private ConcurrentLinkedQueue<StreamCollectionListener<ClientVersionsDocument>> versionsDocumentCollectionListener;


    private static final String COLLECTION_TYPE_USERS = "users";
    private static final String COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration ";
    private static final String COLLECTION_TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String COLLECTION_TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    Moshi moshi;

    public GlobalStreamCollectionManager(Moshi moshi) {
        this.moshi = moshi;
        usersCollectionListener = new ConcurrentLinkedQueue<>();
        loginConfDocumentCollectionListener = new ConcurrentLinkedQueue<>();
        rolesDocumentCollectionListener = new ConcurrentLinkedQueue<>();
        versionsDocumentCollectionListener = new ConcurrentLinkedQueue<>();
    }


    public void subscribeUserCollection(StreamCollectionListener<UserDocument> listener) {
        if (listener != null && !usersCollectionListener.contains(listener)) {
            usersCollectionListener.add(listener);
        }
    }

    public void subscribeLoginConfCollection(StreamCollectionListener<LoginConfDocument> listener) {
        if (listener != null && !loginConfDocumentCollectionListener.contains(listener)) {
            loginConfDocumentCollectionListener.add(listener);
        }
    }

    public void subscribeRocketChatRolesCollection(StreamCollectionListener<RocketChatRolesDocument> listener) {
        if (listener != null && !rolesDocumentCollectionListener.contains(listener)) {
            rolesDocumentCollectionListener.add(listener);
        }
    }

    public void subscribeClientVersionCollection(StreamCollectionListener<ClientVersionsDocument> listener) {
        if (listener != null && !versionsDocumentCollectionListener.contains(listener)) {
            versionsDocumentCollectionListener.add(listener);
        }
    }

    public Boolean unsubscribeUserCollection(StreamCollectionListener<UserDocument> listener) {
        return usersCollectionListener.remove(listener);
    }

    public Boolean unsubscribeLoginConfCollection(StreamCollectionListener<LoginConfDocument> listener) {
        return loginConfDocumentCollectionListener.remove(listener);
    }


    public Boolean unsubscribeRocketChatRolesCollection(StreamCollectionListener<RocketChatRolesDocument> listener) {
        return rolesDocumentCollectionListener.remove(listener);
    }

    public Boolean unsubscribeClientVersionCollection(StreamCollectionListener<ClientVersionsDocument> listener) {
        return versionsDocumentCollectionListener.remove(listener);
    }

    public void update(JSONObject object, RPC.MsgType type) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_USERS)) {
            updateUsers(object, type);
        } else if (collectionName.equals(COLLECTION_TYPE_ROCKETCHAT_ROLES)) {
            updateRoles(object, type);
        } else if (collectionName.equals(COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF)) {
            updateLoginConfiguration(object, type);
        } else if (collectionName.equals(COLLECTION_TYPE_METEOR_CLIENT_VERSIONS)) {
            updateClientVersions(object, type);
        }
    }

    private void updateUsers(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        for (StreamCollectionListener<UserDocument> userListener : usersCollectionListener) {
            switch (type) {
                case ADDED:
                    UserDocument userDocument = null;
                    try {
                        userDocument = getUserDocumentAdapter().fromJson(object.optJSONObject("fields").toString()).withId(id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    userListener.onAdded(id,userDocument);
                    break;
                case CHANGED:
                    userListener.onChanged(id, object.optJSONObject("fields"));
                    break;
                case REMOVED:
                    userListener.onRemoved(id);
                    break;
                case OTHER:
                    break;
                default:
                    break;
            }
        }
    }

    private void updateRoles(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");
        for (StreamCollectionListener<RocketChatRolesDocument> rolesListener : rolesDocumentCollectionListener) {
            switch (type) {
                case ADDED:
                    RocketChatRolesDocument rolesDocument = new RocketChatRolesDocument(object.optJSONObject("fields"));
                    rolesListener.onAdded(id, rolesDocument);
                    break;
                case CHANGED:
                    rolesListener.onChanged(id, object.optJSONObject("fields"));
                    break;
                case REMOVED:
                    rolesListener.onRemoved(id);
                    break;
            }
        }
    }

    private void updateLoginConfiguration(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");
        for (StreamCollectionListener<LoginConfDocument> loginConfListener : loginConfDocumentCollectionListener) {
            switch (type) {
                case ADDED:
                    LoginConfDocument loginConfDocument = new LoginConfDocument(object.optJSONObject("fields"));
                    loginConfListener.onAdded(id, loginConfDocument);
                    break;
                case CHANGED:
                    loginConfListener.onChanged(id, object.optJSONObject("fields"));
                    break;
                case REMOVED:
                    loginConfListener.onRemoved(id);
                    break;
            }
        }
    }

    public void updateClientVersions(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        for (StreamCollectionListener<ClientVersionsDocument> clientVersionListener : versionsDocumentCollectionListener) {
            switch (type) {
                case ADDED:
                    ClientVersionsDocument clientVersionsDocument = new ClientVersionsDocument(object.optJSONObject("fields"));
                    clientVersionsDocument.setId(id);
                    clientVersionListener.onAdded(id, clientVersionsDocument);
                    break;
                case CHANGED:
                    clientVersionListener.onChanged(id, object.optJSONObject("fields"));
                    break;
                case REMOVED:
                    clientVersionListener.onRemoved(id);
                    break;
            }
        }
    }

    public enum Type {
        OTHER_COLLECTION,
        GLOBAL_COLLECTION
    }

    public static Type getCollectionType(JSONObject object) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_USERS) ||
                collectionName.equals(COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF) ||
                collectionName.equals(COLLECTION_TYPE_METEOR_CLIENT_VERSIONS) ||
                collectionName.equals(COLLECTION_TYPE_ROCKETCHAT_ROLES)) {
            return Type.GLOBAL_COLLECTION;
        } else {
            return Type.OTHER_COLLECTION;
        }
    }


    private JsonAdapter<UserDocument> userDocumentAdapter;

    private JsonAdapter<UserDocument> getUserDocumentAdapter() {
        if (userDocumentAdapter == null) {
            userDocumentAdapter = moshi.adapter(UserDocument.class);
        }
        return userDocumentAdapter;
    }

}
