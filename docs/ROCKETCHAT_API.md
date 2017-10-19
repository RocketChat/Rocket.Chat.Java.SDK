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
        client.login("username", "password", this);
    }
    
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        client.getSubscriptions(this);
    }
    
```

**2. loginUsingToken**

- Make sure you have implemented _LoginListener_ interface.

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        client.getSubscriptions(this);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        client.loginUsingToken("token",this);
    }
```

**3. getMyUserId**

- In this class, make sure you have implemented _LoginListener_ interface, which returns callback when login is invoked.
- Same interface implementation should be followed for remaining methods, as login listener interface is used everywhere.

```
@Override
    public void onLogin(TokenObject token, ErrorObject error) {
        client.getSubscriptions(this);

        System.out.println("My userid is "+ client.getMyUserId());
    }
```  
    
**4. getMyUserName**

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        client.getSubscriptions(this);

        System.out.println("My username is "+ client.getMyUserName());
    }
```    

**5. getChatRoomFactory**

- Make sure you have implemented _LoginListener_ interface.

```
@Override
    public void onLogin(TokenObject token, ErrorObject error) {
        client.getSubscriptions(this);
    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        ChatRoomFactory factory = client.getChatRoomFactory();   //client.is used for creating rooms from subscriptions/ rooms retured by either getSubscriptions or getRooms API
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

**7. getPermissions**

- Make sure you have implemented _GetSubscriptionListener_ interface.

```
    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        client.getSubscriptions(this);
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
        client.getPublicSettings(this);
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
        client.getUserRoles(this);
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
   client.createPublicGroup("MyPublicGroup", null, false, new RoomListener.GroupListener() {
            public void onCreateGroup(String roomId, ErrorObject error) {
                System.out.println("Created public Group with roomId "+ roomId);
            }
    });
```

**14. createPrivateGroup**

- Do not implement interface, try to create callback corresponding to method directly. 

```
   // Params : Group name, array of usernames to join directly, listener 
        client.createPrivateGroup("MyPrivateGroup", null, new RoomListener.GroupListener() {
            public void onCreateGroup(String roomId, ErrorObject error) {
                
            }
        });
```

**15. joinPublicGroup**

- Create SimpleListener Callback directly.

```
        client.joinPublicGroup("roomId", null, new SimpleListener() {
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
        client.setStatus(UserObject.Status.ONLINE, new SimpleListener() {
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
        client.subscribeActiveUsers(new SubscribeListener() {
            public void onSubscribe(Boolean isSubscribed, String subId) {
                System.out.println("Subscribed to active users successfully");
            }
        });
        
```

**18. subscribeUserData**

- Directly pass subscribeListener interface for success callback.

```
        client.subscribeUserData(new SubscribeListener() {
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
        client.logout(new SimpleListener() {
            public void callback(Boolean success, ErrorObject error) {
                System.out.println("Logged out from the server");
            }
        });

```
