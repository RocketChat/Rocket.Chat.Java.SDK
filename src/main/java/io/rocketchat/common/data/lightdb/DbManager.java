package io.rocketchat.common.data.lightdb;

import io.rocketchat.common.data.lightdb.collection.Collection;
import io.rocketchat.common.data.lightdb.document.UserDocument;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by sachin on 11/8/17.
 */
public class DbManager extends Observable{

    Collection<String, UserDocument> usersCollection;

    private static final String TYPE_USERS = "users";
    private static final String TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    public DbManager(){
        usersCollection= new Collection<>();
    }

    public UserObject getUser(String userId) {
        return usersCollection.get(userId);
    }

    public ArrayList<UserDocument> getUserCollection(){
        return usersCollection.getData();
    }

    public void update(JSONObject object, RPC.MsgType type) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(TYPE_USERS)){
            updateUsers(object,type);
        }
    }

    private void updateUsers(JSONObject object, RPC.MsgType type){
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
                setChanged();
                notifyObservers(usersCollection.get(id));
                break;
            case REMOVED:
                usersCollection.remove(id);
                setChanged();
                notifyObservers();
                break;
            case OTHER:
                break;
        }
    }

    public enum Type {
        STREAM,
        COLLECTION
    }

    public static Type getCollectionType(JSONObject object) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(TYPE_USERS) ||
            collectionName.equals(TYPE_METEOR_ACCOUNTS_LOGIN_CONF) ||
            collectionName.equals(TYPE_METEOR_CLIENT_VERSIONS)  ||
            collectionName.equals(TYPE_ROCKETCHAT_ROLES)) {
            return Type.COLLECTION;
        } else {
            return Type.STREAM;
        }
    }
}
