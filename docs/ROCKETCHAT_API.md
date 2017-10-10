Following methods are provided by RocketChatAPI class

- login (It is used for login using username and password)
- loginUsingToken (It is used to login using token, or resuming previous login)
- getMyUserId  (Use this method after login, it is used to get user id of logged in user)
- getMyUserName ( Use this method after login, it is used to get username of logged in user)
- getChatRoomFactory (It's a logical factory that holds all rooms and used to create, receive room (by name or id), or remove rooms from local memory)
- getDbManager (It is used to get dbManager which consists of storage of collections, eg. UserCollection : It stores all user presence automatically))
- getPermissions (Getting permissions available on the server)
- getPublicSettings (Getting public settings available on the server)
- getUserRoles (Getting user roles from the server)
- listCustomEmoji (Listing custom emoji, mainly consists of receiving custom emoji codes)
- getSubscriptions (Getting subscriptions (rooms) from the server, **prefer this method to use rather than getRooms() method** for getting rooms, contains more meaningful data)
- getRooms (Getting rooms from the server)
- createPublicGroup (Creating public group on the server, can provide optional list of users to get included as members)
- createPrivateGroup (Creating private group on the server, can provide optional list of users to get included as members)
- joinPublicGroup (Joining a particular public group)
- setStatus (Set status as ONLINE, OFFLINE, AWAY, BUSY)
- subscribeActiveUsers (subscribe for getting user presence of all associated users, this method should be called after login)
- subscribeUserData (subscribing to the user data, getting user data in more detail, should be called in more detail)
- logout (Logging out from the server)

### Important Notes
**Get rid of callback hell:** Best way to write down callbacks is to let given Class implement the interface and passing it's instance to the room method as a callback parameter. Same should be followed for other methods. </br>
**Receive no Callback:** Pass null in case callback receive is not important.

### Methods : RocketChatAPI
**1. login**

- Make sure you have implemented _LoginListener_ interface.

```
    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login("username", "password", this);
    }
    
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);
    }
    
```

**2. loginUsingToken**

- Make sure you have implemented _LoginListener_ interface.

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken("token",this);
    }
```

**3. getMyUserId**

- In this class, make sure you have implemented _LoginListener_ interface, which returns callback when login is invoked.
- Same interface implementation should be followed for remaining methods, as login listener interface is used everywhere.

```
@Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);

        System.out.println("My userid is "+ api.getMyUserId());
    }
```  
    
**4. getMyUserName**

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);

        System.out.println("My username is "+ api.getMyUserName());
    }
```    

**5. getChatRoomFactory**

- Make sure you have implemented _LoginListener_ interface.

```
@Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);
    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        ChatRoomFactory factory = api.getChatRoomFactory();   //Api is used for creating rooms from subscriptions/ rooms retured by either getSubscriptions or getRooms API
        room = factory.createChatRooms(subscriptions).getChatRoomByName("general");
    }
    
```    

- There are various factory API's available to manipulate rooms, once they are created.
- Those API's can be given as follow (Once chatrooms are created by passing subscriptions)

- Getting all chat rooms from the server

```
List <RocketChatAPI.ChatRoom> rooms = factory.getChatRooms();
```


- Getting chat room by name

```
RocketChatAPI.ChatRoom room = factory.getChatRoomByName("general");
```

- Getting chat room by id

```
RocketChatAPI.ChatRoom room = factory.getChatRoomById("abcd123");
```

- Add chat room 

```

factory.addChatRoom(subscriptions.get(0));

//or

factory.addChatRoom(rooms.get(0));

```

- Remove chat room

```
factory.removeChatRoomById("room_id");

//or 

factory.removeChatRoomByName(room);

```

**6. getDbManager**

- Whenever subscribed to ActiveUsers and UserData (see method number 17 and 18), server will keep sending data to a user about other users presence.
- Other users can be thought as _Users in the vicinity_.
- Those users automatically gets added in lightweight memory database.
- This method provides a way to access those users by their id or registering a observer when status of a user changes from ONLINE TO OFFLINE.

- Getting user status from id (method returns doc which also contains other information of a given user)

```
   api.subscribeUserData(null);
   api.subscribeActiveUsers(null);
   //Make sure you are subscribed by using above code
   UserDocument user = api.getDbManager().getUserCollection().get("userid");
   System.out.println("UserName is " + user.getName());
   System.out.println("User status is "+ user.getStatus());
   System.out.println("User avatar url is "+ user.getAvatarUrl());
```


- Observe for status change of a particular user by providing his/her user-id

```
        api.subscribeUserData(null);
        api.subscribeActiveUsers(null);
        api.getDbManager().getUserCollection().register("user_id", new 
            Collection.Observer<UserDocument>() {
            public void onUpdate(Collection.Type type, UserDocument user) {
                switch (type) {
                    case ADDED:
                        System.out.println("user has been added, status is "+ user.getStatus());
                        break;
                    case CHANGED:
                        System.out.println("user has been changed, status is "+ user.getStatus());
                        break;
                    case REMOVED:
                        System.out.println("user has been removed, status is "+ user.getStatus());
                        break;
                }
            }
        });

```

- Observe all collections for document changes.

```
        api.subscribeUserData(null);
        api.subscribeActiveUsers(null);
        api.subscribeClientVersions(null);
        api.subscribeLoginConf(null);
        api.subscribeUserRoles(null);
        api.getDbManager().addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg !=null) {
                    UserDocument document = (UserDocument) arg;
                    System.out.println("Username is "+ document.getName());
                    System.out.println("Status of a user is " + document.getStatus());
                }
            }
        });

```

- Observe particular collection for change
1. This is a loginConfiguration collection change.

```
        api.subscribeLoginConf(null);
        api.getDbManager().getLoginConfDocumentCollection().addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                LoginConfDocument document = (LoginConfDocument) arg;
                System.out.println("New document name is "+ document.getService());
                System.out.println("Client Id is " + document.getClientId());
                System.out.println("App id is " + document.getAppId());
                System.out.println("Consumer key is " + document.getConsumerKey());
            }
        });
```

2. This is a client version change
```
        api.subscribeClientVersions(null);
        api.getDbManager().getVersionsDocumentCollection().addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                ClientVersionsDocument document = (ClientVersionsDocument) arg;
                System.out.println("id is " + document.getId());
                System.out.println("version is " + document.getVersion());
            }
        });
```

3. This is change to the user roles

```
        api.subscribeUserRoles(null);
        api.getDbManager().getRolesDocumentCollection().addObserver(new Observer() {
          public void update(Observable o, Object arg) {
              RocketChatRolesDocument document = (RocketChatRolesDocument) arg;
              System.out.println("Role name is " + document.getName());
              System.out.println("Description is " + document.getDescription());
              System.out.println("Scope is " + document.getScope());
              System.out.println("Updated at " + document.getUpdatedAt());
          }
        });
```

**7. getPermissions**

- Make sure you have implemented _GetSubscriptionListener_ interface.

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);
    }
    
    @Override
    public void onGetPermissions(List<Permission> permissions, ErrorObject error) {
        System.out.println("Got here list of permissions");
    }
```

**8. getPublicSettings**

- Make sure you have implemented _AccountListener.getPublicSettingsListener_ interface.

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getPublicSettings(this);
    }
    
    @Override
    public void onGetPublicSettings(List<PublicSetting> settings, ErrorObject error) {
        super.onGetPublicSettings(settings, error);
    }
    
```

**9. getUserRoles**

- Make sure you have implemented _UserListener.getUserRoleListener_ interface.

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getUserRoles(this);
    }
    
    @Override
    public void onUserRoles(List<UserObject> users, ErrorObject error) {
        
    }
    
```


**10. listCustomEmoji**

- Make sure you have implemented _EmojiListener_ interface.

```
    @Override
    public void onListCustomEmoji(List<Emoji> emojis, ErrorObject error) {

    }
    
```

**11. getSubscriptions**

- Make sure you have implemented _GetSubscriptionListener_ interface.

```
    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
    
    }
    
```

**12. getRooms**

- Make sure you have implemented _RoomListener.GetRoomListener_ interface.

```
    @Override
    public void onGetRooms(List<RoomObject> rooms, ErrorObject error) {

    }
```

**13. createPublicGroup**

- Do not implement interface, try to create callback corresponding to method directly. 

```
   // Params : Group name, array of usernames to join directly, read only or now , listener 
   api.createPublicGroup("MyPublicGroup", null, false, new RoomListener.GroupListener() {
            public void onCreateGroup(String roomId, ErrorObject error) {
                System.out.println("Created public Group with roomId "+ roomId);
            }
    });
```

**14. createPrivateGroup**

- Do not implement interface, try to create callback corresponding to method directly. 

```
   // Params : Group name, array of usernames to join directly, listener 
        api.createPrivateGroup("MyPrivateGroup", null, new RoomListener.GroupListener() {
            public void onCreateGroup(String roomId, ErrorObject error) {
                
            }
        });
```

**15. joinPublicGroup**

- Create SimpleListener Callback directly.

```
        api.joinPublicGroup("roomId", null, new SimpleListener() {
            public void callback(Boolean success, ErrorObject error) {
                if (success) {
                    System.out.println("room joined successfully");
                }
            }
        });

```

**16. setStatus**

- Create SimpleListener Callback directly.

```
//Status can be ONLINE, OFFLINE, BUSY, AWAY
        api.setStatus(UserObject.Status.ONLINE, new SimpleListener() {
            public void callback(Boolean success, ErrorObject error) {
                if (success) {
                    System.out.println("Status set to online");
                }
            }
        });
        
```

**17. subscribeActiveUsers**

- Directly pass subscribeListener interface for success callback.

```
        api.subscribeActiveUsers(new SubscribeListener() {
            public void onSubscribe(Boolean isSubscribed, String subId) {
                System.out.println("Subscribed to active users successfully");
            }
        });
        
```

**18. subscribeUserData**

- Directly pass subscribeListener interface for success callback.

```
        api.subscribeUserData(new SubscribeListener() {
            public void onSubscribe(Boolean isSubscribed, String subId) {
                System.out.println("Subscribed to user data");
            }
        });
```

19. subscribeUserRoles
- Used to get different user roles 
```java

```

20. subscribeLoginConf
- Used to get different login configurations used for facebook, twitter, github etc

```java

```
21. subscribeClientVersions
- Used to get different client versions

```java


```

**19. logout**

- Used for logging out from the server.

```
        api.logout(new SimpleListener() {
            public void callback(Boolean success, ErrorObject error) {
                System.out.println("Logged out from the server");
            }
        });

```
