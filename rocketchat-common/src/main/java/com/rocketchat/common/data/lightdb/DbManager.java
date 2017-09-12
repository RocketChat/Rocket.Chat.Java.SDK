package com.rocketchat.common.data.lightdb;

import com.rocketchat.common.data.lightdb.collection.Collection;
import com.rocketchat.common.data.lightdb.document.ClientVersionsDocument;
import com.rocketchat.common.data.lightdb.document.LoginConfDocument;
import com.rocketchat.common.data.lightdb.document.RocketChatRolesDocument;
import com.rocketchat.common.data.lightdb.document.UserDocument;
import com.rocketchat.common.data.rpc.RPC;

import java.util.Observable;

import org.json.JSONObject;

/**
 * Created by sachin on 11/8/17.
 */
public class DbManager extends Observable {

    private Collection<String, UserDocument> usersCollection;
    private Collection<String, LoginConfDocument> loginConfDocumentCollection;
    private Collection<String, RocketChatRolesDocument> rolesDocumentCollection;
    private Collection<String, ClientVersionsDocument> versionsDocumentCollection;


    private static final String COLLECTION_TYPE_USERS = "users";
    private static final String COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String COLLECTION_TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String COLLECTION_TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    public DbManager() {
        usersCollection = new Collection<>();
        loginConfDocumentCollection = new Collection<>();
        rolesDocumentCollection = new Collection<>();
        versionsDocumentCollection = new Collection<>();
    }

    public Collection<String, UserDocument> getUserCollection() {
        return usersCollection;
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
                UserDocument userDocument = new UserDocument(object.optJSONObject("fields"));
                userDocument.setUserId(id);
                usersCollection.add(id, userDocument);
                setChanged();
                notifyObservers(userDocument);
                break;
            case CHANGED:
                usersCollection.get(id).update(object.optJSONObject("fields"));
                UserDocument document = usersCollection.get(id);
                usersCollection.update(id, document);
                setChanged();
                notifyObservers(document);
                break;
            case REMOVED:
                usersCollection.remove(id);
                setChanged();
                notifyObservers();
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
                rolesDocumentCollection.add(id, rolesDocument);
                break;
            case CHANGED:
                rolesDocumentCollection.get(id).update(object.optJSONObject("fields"));
                RocketChatRolesDocument document = rolesDocumentCollection.get(id);
                rolesDocumentCollection.update(id, document);
                break;
            case REMOVED:
                rolesDocumentCollection.remove(id);
                break;
        }
    }

    private void updateLoginConfiguration(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        switch (type) {
            case ADDED:
                LoginConfDocument loginConfDocument = new LoginConfDocument(object.optJSONObject("fields"));
                loginConfDocumentCollection.add(id, loginConfDocument);
                break;
            case CHANGED:
                loginConfDocumentCollection.get(id).update(object.optJSONObject("fields"));
                LoginConfDocument document = loginConfDocumentCollection.get(id);
                loginConfDocumentCollection.update(id, document);
                break;
            case REMOVED:
                loginConfDocumentCollection.remove(id);
                break;
        }
    }

    public void updateClientVersions(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        switch (type) {
            case ADDED:
                ClientVersionsDocument clientVersionsDocument = new ClientVersionsDocument(object.optJSONObject("fields"));
                versionsDocumentCollection.add(id, clientVersionsDocument);
                break;
            case CHANGED:
                versionsDocumentCollection.get(id).update(object.optJSONObject("fields"));
                ClientVersionsDocument document = versionsDocumentCollection.get(id);
                versionsDocumentCollection.update(id, document);
                break;
            case REMOVED:
                versionsDocumentCollection.remove(id);
                break;
        }
    }

    public enum Type {
        STREAM_COLLECTION,
        COLLECTION
    }

    public static Type getCollectionType(JSONObject object) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_USERS) ||
                collectionName.equals(COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF) ||
                collectionName.equals(COLLECTION_TYPE_METEOR_CLIENT_VERSIONS) ||
                collectionName.equals(COLLECTION_TYPE_ROCKETCHAT_ROLES)) {
            return Type.COLLECTION;
        } else {
            return Type.STREAM_COLLECTION;
        }
    }
}
