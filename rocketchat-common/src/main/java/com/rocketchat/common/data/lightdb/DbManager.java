package com.rocketchat.common.data.lightdb;

import com.rocketchat.common.data.CommonJsonAdapterFactory;
import com.rocketchat.common.data.lightdb.collection.Collection;
import com.rocketchat.common.data.lightdb.document.UserDocument;
import com.rocketchat.common.data.rpc.RPC;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Observable;
import org.json.JSONObject;

/**
 * Created by sachin on 11/8/17.
 */
public class DbManager extends Observable {

    private final JsonAdapter<UserDocument> adapter;
    private Collection<String, UserDocument> usersCollection;

    private static final String COLLECTION_TYPE_USERS = "users";
    private static final String COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String COLLECTION_TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String COLLECTION_TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    // TODO - we should not need Moshi here, we should pass the object already serialized.
    public DbManager(Moshi moshi) {
        adapter = moshi.adapter(UserDocument.class);
        usersCollection = new Collection<>();
    }

    public Collection<String, UserDocument> getUserCollection() {
        return usersCollection;
    }

    public void update(JSONObject object, RPC.MsgType type) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_USERS)) {
            updateUsers(object, type);
        }
    }

    private void updateUsers(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");
        UserDocument document;

        try {
            switch (type) {
                case ADDED:
                    document = adapter.fromJson(object.optJSONObject("fields").toString());
                    document = document.withId(id);
                    usersCollection.add(id, document);
                    setChanged();
                    notifyObservers(document);
                    break;
                case CHANGED:
                    UserDocument newDocument = adapter.fromJson(object.optJSONObject("fields").toString());
                    document = usersCollection.get(id);
                    document = document.update(newDocument);
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
        } catch (IOException e) {
            // TODO - Better error handling.
            e.printStackTrace();
        }
        System.out.println("DB SIZE: " + usersCollection.size());
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
