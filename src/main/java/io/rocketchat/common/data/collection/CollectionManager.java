package io.rocketchat.common.data.collection;

import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONObject;

import java.util.Observable;

/**
 * Created by sachin on 11/8/17.
 */
public class CollectionManager extends Observable{

    Collection <String, UserObject> usersCollection;

    private static final String TYPE_USERS = "users";
    private static final String TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    public CollectionManager(){
        usersCollection= new Collection<>();
    }

    public Collection<String, UserObject> getUsersCollection() {
        return usersCollection;
    }

    public void update(JSONObject object, RPC.MsgType type) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(TYPE_USERS)){
//            updateUsers(object,type);
            System.out.println("I got where I wanted to go");
        }
    }

    private void updateUsers(JSONObject object, RPC.MsgType type){
        String id = object.optString("id");
        UserObject user = new UserObject(object.optJSONObject("fields"));
        user.setUserId(id);

        switch (type) {
            case ADDED:
                usersCollection.add(id, user);
                break;
            case CHANGED:
                usersCollection.update(id, user);
                break;
            case REMOVED:
                usersCollection.remove(id);
                break;
            case OTHER:
                break;
        }
        setChanged();
        notifyObservers(user);
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
