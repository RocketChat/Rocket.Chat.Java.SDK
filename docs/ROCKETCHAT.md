### Overview

- Primary requirement is to have **URL** of hosted server.
- The process of connecting to server and registration must be done by following below steps

#### 1. Connecting to server
- Process of connecting to server can be done by 3 ways.
1. By providing instance of ConnectListener to the _connect_ method

```
public class Main{


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(new ConnectListener() {
            @Override
            public void onConnect(String sessionID) {
                System.out.println("Connected to server");
            }

            @Override
            public void onDisconnect(boolean closedByServer) {

            }

            @Override
            public void onConnectError(Exception websocketException) {

            }
        });
    }

    public static void main(String [] args){
        new Main().call();
    }
}

```

2. By providing instance of CoreAdapter to the _connect_ method (Instance of CoreAdapter can be passed to any method which require callback, implement required callback inside adapter)

```
public class Main{


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(new CoreAdapter(){
            @Override
            public void onConnect(String sessionID) {
                System.out.println("Connected to server");
                super.onConnect(sessionID);
            }

            @Override
            public void onDisconnect(boolean closedByServer) {
                super.onDisconnect(closedByServer);
            }

            @Override
            public void onConnectError(Exception websocketException) {
                super.onConnectError(websocketException);
            }
        });
    }

    public static void main(String [] args){
        new Main().call();
    }
}
```

3. Implementing required interface and passing instance of class to the _connect_ method (My favorite)

```

public class Main implements ConnectListener{


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }
}

```

#### 2. Logging in
- Above ways can be replicated for logging in.
- Login can be done using _username_ and _password_ as well as using Token for next time Login.
- It must be done after connecting to server.

1. Using _username_ and _password_

```
public class Main implements ConnectListener, LoginListener {


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";
    private static String username="";
    private static String password="";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login(username,password,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        if (error==null) {
            System.out.println("Logged in successfully, returned token "+ token.getAuthToken());
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }

}

```

2. Using _token_ for resume login
- Save token received from first time login using _username_ and _password_ in database or file.
- Use it for next time login with server.

```
public class Main implements ConnectListener, LoginListener {

    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";
    private static String token="xxx"; //Your token after first login goes here

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken(token,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        if (error==null) {
            System.out.println("Logged in successfully, returned token "+ token.getAuthToken());
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }

}
```

#### 3. Getting rooms/subscriptions
- Both will returns List of rooms that user has joined or subscribed. 
- There are generally 3 types of rooms, one-to-one chat (ONE_TO_ONE), Private group (PRIVATE), public group (PUBLIC)

1. Getting rooms

```
public class Main implements ConnectListener, LoginListener, RoomListener.GetRoomListener {


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";
    private static String token="";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken(token,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        if (error==null) {
            System.out.println("Logged in successfully, returned token "+ token.getAuthToken());
            api.getRooms(this);
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }


    @Override
    public void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error) {
        if (error==null){
            for (RoomObject room : rooms){
                System.out.println("Room name is "+room.getRoomName());
                System.out.println("Room id is "+room.getRoomId());
                System.out.println("Room topic is "+room.getTopic());
                System.out.println("Room type is "+ room.getRoomType());
                
            }
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }
    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }

}
```

2. Getting subscriptions

```
public class Main implements ConnectListener, LoginListener, SubscriptionListener.GetSubscriptionListener {

    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";
    private static String token="";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken(token,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        if (error==null) {
            System.out.println("Logged in successfully, returned token "+ token.getAuthToken());
            api.getSubscriptions(this);
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {

        if (error==null){
            for (SubscriptionObject room : subscriptions){
                System.out.println("Room name is "+room.getRoomName());
                System.out.println("Room id is "+room.getRoomId());
                System.out.println("Room created at "+room.getRoomCreated());
                System.out.println("Room type is "+ room.getRoomType());
            }
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }
}

```

#### 4. Creating logical rooms
- ChatRoomFactory is used to create list of logical rooms to communicate with server.
- Factory class can be used to get ChatRoom by Name or roomId once constructed.  
- RocketChatAPI.ChatRoom class instance represents a unit logical room that can be used to perform different operations on the data. 

```
public class Main implements ConnectListener, LoginListener, SubscriptionListener.GetSubscriptionListener {


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";
    private static String token="";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken(token,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        if (error==null) {
            System.out.println("Logged in successfully, returned token "+ token.getAuthToken());
            api.getSubscriptions(this);
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {

        //Creating Logical ChatRooms using factory class
        ChatRoomFactory factory=api.getFactory();

        /* Number of operations can be performed on below room object like
         * sendMessage
         * deleteMessage
         * pinMessage
         * leaveRoom
         * starRoom
         * hideRoom
         * etc
         * Note : This ChatRoom is logical, meant to perform operations
         */

        RocketChatAPI.ChatRoom room=factory.createChatRooms(subscriptions).getChatRoomByName("general");   //This should exist on server

        //sending sample message to general

        room.sendMessage("Hi there, message sent via code..");


        /*
        Getting list of rooms from factory class after creating logical ChatRooms
         */

        ArrayList <RocketChatAPI.ChatRoom> rooms=factory.getChatRooms();

        for (RocketChatAPI.ChatRoom myroom : rooms){
            System.out.println("Room id is "+myroom.getRoomData().getRoomId());
            System.out.println("room name is "+myroom.getRoomData().getRoomName());
            System.out.println("Room type is "+myroom.getRoomData().getRoomType());
        }

    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }
}
```

#### 5. Further communication with server

#### 6. Handling re-connection with server
- `reconnect` method in `RocketChatAPI` class can be used for reconnecting to the server.

I. Manual reconnection
- Set reconnection to null before connecting to server.  

```java
    api.setReconnectionStrategy(null);
    api.connect(this);
```
- After disconnect event, reconnect to the server

```java
    @Override
    public void onDisconnect(boolean closedByServer) {
            api.reconnect();
            System.out.println("Disconnected from server");
    }

```

II. Automatic reconnection
- Pass reconnection object while setting reconnection strategy

```java
        int maxAttempts=10;    //Number of attemps are 10
        int timeInterval=5000; // in milliseconds, reconnection will be called after 5 seconds
        api.setReconnectionStrategy(new ReconnectionStrategy(maxAttempts,timeInterval));  
```

#### 7. Log out
- 
- 
