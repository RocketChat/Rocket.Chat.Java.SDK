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

### Methods : RocketChatAPI
**1. login**

```
    api.singleConnect()
      .thenCompose(v -> api.login("username", "password"))
      .thenCompose(token -> api.getSubscriptions());
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
        api.loginUsingToken("token");
    }
```

**3. getMyUserId**

```
    String userId = api.singleConnect()
           .thenCompose(v -> api.login("username", "password"))
           .thenCompose(token -> api.getSubscriptions(this))
           .thenCompose(s -> api.getMyUserId())
           .get()
    
    System.out.println("My userid is "+ userId);
```  
    
**4. getMyUserName**

```
    String username = api.singleConnect()
       .thenCompose(v -> api.login("username", "password"))
       .thenCompose(token -> api.getSubscriptions(this))
       .thenCompose(s -> api.getMyUserName())
       .get()

    System.out.println("My username is "+ api.getMyUserName());
```    

**5. getChatRoomFactory**

- Make sure you have implemented _LoginListener_ interface.

```
    List<SubscriptionObject> subscriptions = api.singleConnect()
               .thenCompose(v -> api.login("username", "password"))
               .thenCompose(token -> api.getSubscriptions())
               .get();
    ChatRoomFactory factory = api.getChatRoomFactory();   //Api is used for creating rooms from subscriptions/ rooms retured by either getSubscriptions or getRooms API
    room = factory.createChatRooms(subscriptions).getChatRoomByName("general");
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
   UserDocument user = api.getDbManager().getUserCollection().get("userid");
   System.out.println("UserName is " + user.getName());
   System.out.println("User status is "+ user.getStatus());
   System.out.println("User avatar url is "+ user.getAvatarUrl());
```


- Observe for status change of a particular user by providing his/her user-id

```
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

- Observe all users for status changes.

```
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

**7. getPermissions**

```
    List<Permission> permissions = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.getPermissions())
                   .get();
    System.out.println("Got here list of permissions");
```

**8. getPublicSettings**

```
    List<PublicSetting> settings = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.getPublicSettings())
                   .get();
```

**9. getUserRoles**

```
    List<UserObject> users = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.getUserRoles())
                   .get();
```


**10. listCustomEmoji**

```
    List<Emoji> emojis = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.listCustomEmoji())
                   .get();
```

**11. getSubscriptions**

```
    List<SubscriptionObject> subscriptions = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.getSubscriptions())
                   .get();
```

**12. getRooms**

```
    List<RoomObject> rooms = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.getRooms())
                   .get();
```

**13. createPublicGroup**

```
    String roomId = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                    // Params : Group name, array of usernames to join directly, read only or now 
                   .thenCompose(token -> api.createPublicGroup("MyPublicGroup", null, false))
                   .get();
    System.out.println("Created public Group with roomId " + roomId);
```

**14. createPrivateGroup**

```
    String roomId = api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                    // Params : Group name, array of usernames to join directly
                   .thenCompose(token -> api.createPrivateGroup("MyPrivateGroup", null))
                   .get();
```

**15. joinPublicGroup**

```
    api.singleConnect()
                   .thenCompose(v -> api.login("username", "password"))
                   .thenCompose(token -> api.joinPublicGroup("roomId", null))
                   .thenAccept(success -> {
                       if (success) {
                           System.out.println("room joined successfully");
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

**19. logout**

- Used for logging out from the server.

```
        api.logout().thenAccept(success -> {
            System.out.println("Logged out from the server");
        });
```
