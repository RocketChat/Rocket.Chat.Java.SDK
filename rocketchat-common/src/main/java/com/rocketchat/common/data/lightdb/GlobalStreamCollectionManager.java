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
import org.json.JSONObject;

/**
 * Created by sachin on 11/8/17.
 */
public class GlobalStreamCollectionManager {

    StreamCollectionListener<UserDocument> usersCollection;
    StreamCollectionListener<LoginConfDocument> loginConfDocumentCollection;
    StreamCollectionListener<RocketChatRolesDocument> rolesDocumentCollection;
    StreamCollectionListener<ClientVersionsDocument> versionsDocumentCollection;


    private static final String COLLECTION_TYPE_USERS = "users";
    private static final String COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String COLLECTION_TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String COLLECTION_TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    Moshi moshi;

    public GlobalStreamCollectionManager(Moshi moshi) {
        this.moshi = moshi;
    }


    public void setUsersCollection(StreamCollectionListener<UserDocument> usersCollection) {
        this.usersCollection = usersCollection;
    }

    public void setLoginConfDocumentCollection(StreamCollectionListener<LoginConfDocument> loginConfDocumentCollection) {
        this.loginConfDocumentCollection = loginConfDocumentCollection;
    }

    public void setRolesDocumentCollection(StreamCollectionListener<RocketChatRolesDocument> rolesDocumentCollection) {
        this.rolesDocumentCollection = rolesDocumentCollection;
    }

    public void setVersionsDocumentCollection(StreamCollectionListener<ClientVersionsDocument> versionsDocumentCollection) {
        this.versionsDocumentCollection = versionsDocumentCollection;
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

        switch (type) {
            case ADDED:
                UserDocument userDocument = null;
                try {
                    userDocument = getUserDocumentAdapter().fromJson(object.optJSONObject("fields").toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                usersCollection.onAdded(userDocument);
                break;
            case CHANGED:
                usersCollection.onChanged(object.optJSONObject("fields"));
                break;
            case REMOVED:
                usersCollection.onRemoved(id);
                break;
            case OTHER:
                break;
            default:
                break;
        }
    }

    private void updateRoles(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        switch (type) {
            case ADDED:
                RocketChatRolesDocument rolesDocument = new RocketChatRolesDocument(object.optJSONObject("fields"));
                rolesDocumentCollection.onAdded(rolesDocument);
                break;
            case CHANGED:
                rolesDocumentCollection.onChanged(object.optJSONObject("fields"));
                break;
            case REMOVED:
                rolesDocumentCollection.onRemoved(id);
                break;
        }
    }

    private void updateLoginConfiguration(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        switch (type) {
            case ADDED:
                LoginConfDocument loginConfDocument = new LoginConfDocument(object.optJSONObject("fields"));
                loginConfDocumentCollection.onAdded(loginConfDocument);
                break;
            case CHANGED:
                loginConfDocumentCollection.onChanged(object.optJSONObject("fields"));
                break;
            case REMOVED:
                loginConfDocumentCollection.onRemoved(id);
                break;
        }
    }

    public void updateClientVersions(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        switch (type) {
            case ADDED:
                ClientVersionsDocument clientVersionsDocument = new ClientVersionsDocument(object.optJSONObject("fields"));
                versionsDocumentCollection.onAdded(clientVersionsDocument);
                break;
            case CHANGED:
                versionsDocumentCollection.onChanged(object.optJSONObject("fields"));
                break;
            case REMOVED:
                versionsDocumentCollection.onRemoved(id);
                break;
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
