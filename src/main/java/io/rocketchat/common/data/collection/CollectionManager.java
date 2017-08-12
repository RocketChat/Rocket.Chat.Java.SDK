package io.rocketchat.common.data.collection;

import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by sachin on 11/8/17.
 */
public class CollectionManager extends Observable{

    Collection <String, UserDocument> usersCollection;

    private static final String TYPE_USERS = "users";
    private static final String TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    public CollectionManager(){
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
                usersCollection.add(id, userDocument);
                setChanged();
                notifyObservers(userDocument);
                System.out.println("Added got called");
                break;
            case CHANGED:
                usersCollection.get(id).update(object.optJSONObject("fields"));
                setChanged();
                notifyObservers(usersCollection.get(id));
                System.out.println("Changed got called");
                break;
            case REMOVED:
                usersCollection.remove(id);
                setChanged();
                notifyObservers();
                System.out.println("Removed got called");
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
