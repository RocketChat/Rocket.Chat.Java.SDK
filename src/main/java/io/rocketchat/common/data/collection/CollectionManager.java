package io.rocketchat.common.data.collection;

import io.rocketchat.common.data.model.UserObject;

import java.util.ArrayList;

/**
 * Created by sachin on 11/8/17.
 */
public class CollectionManager {

    Collection <String, UserObject> usersCollection;

    private static final String TYPE_USERS = "users";
    private static final String TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    CollectionManager(){
        usersCollection= new Collection<>();
    }

    public ArrayList<UserObject> getUsersCollection() {
        return usersCollection.getData();
    }
}
