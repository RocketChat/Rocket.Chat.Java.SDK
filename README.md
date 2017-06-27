Rocket.Chat.Java.SDK
=====================================
Overview
--------
This SDK is divided into two parts
1. Core SDK
2. LiveChat SDK

Current development approach is focused on **LiveChat SDK**.

License
-------
MIT

Gradle
------
For java 

```Gradle

dependencies {
    compile 'io.rocketchat:rocketchatjavasdk:0.3.2'
}
```
For android 

```Gradle
compile ('io.rocketchat:rocketchatjavasdk:0.3.2'){
        exclude group :'org.json', module: 'json'
}
```

[ ![Download](https://api.bintray.com/packages/sacoo7/Maven/RocketChat-SDK/images/download.svg) ](https://bintray.com/sacoo7/Maven/RocketChat-SDK/_latestVersion)


Documentation
-------------
### Important Note :
- Before reading the documentation, go through all terms associated with [LiveChat (Agent, Departments etc)](https://rocket.chat/docs/administrator-guides/livechat/).
- Make sure **LiveChat is properly configured** on the server.

### Overview

- LiveChat SDK will consist of RPC for server methods available on hosted or remote rocket.chat server.</br>
- Primary requirement is to have **url** of hosted server.
- The process of connecting to server and registration must be done by following 3 steps

#### 1. Connecting to server

- Rocket.Chat **Test LiveChat server (Configuration READY)** is hosted under "wss://livechattest.rocket.chat/websocket".</br> 
- Connection to the server can be made as below.

```java
    public class Main implements ConnectListener {
    
        private LiveChatAPI liveChat;
        private static String serverurl="wss://livechattest.rocket.chat/websocket";
    
        public void call(){
            liveChat=new LiveChatAPI(serverurl);
            liveChat.setReconnectionStrategy(null); //null means no reconnection after disconnect.
            liveChat.connect(this);
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
            System.out.println("Disconnected from server");
        }
    
        @Override
        public void onConnectError(Exception websocketException) {
            System.out.println("Connection error with server");
        }
    }
```


#### 2. Registration of a user

- Registration is a one time process.
- It is in order to communicate with agent and is done using **email** and **password**.

```java 

    public class Main implements ConnectListener ,AuthListener.RegisterListener {
    
        private LiveChatAPI liveChat;
        private static String serverurl="wss://livechattest.rocket.chat/websocket";
    
        public void call(){
            liveChat=new LiveChatAPI(serverurl);
            liveChat.setReconnectionStrategy(null);
            liveChat.connect(this); // Will call connect in background, required for non-blocking thread
        }
    
        public static void main(String [] args){
            new Main().call();
        }
    
    
        @Override
        public void onConnect(String sessionID) {
            System.out.println("Connected to server");
            liveChat.registerGuest("aditi","aditi@gmail.com",null,this);
        }
    
        @Override
        public void onDisconnect(boolean closedByServer) {
            System.out.println("Disconnected from server");
        }
    
        @Override
        public void onConnectError(Exception websocketException) {
            System.out.println("Got connect error with the server");
        }
    
        /**
         *
         * @param object Will consist of UserID and AuthToken
         * @param error  Will generate error if registration fails
         */
        @Override
        public void onRegister(GuestObject object, ErrorObject error) {
            if (error==null) {
                System.out.println("registration success, data :: "+object);
            }else{
                System.out.println("registration failure "+error);
            }
        }
    }

```

#### 3. Logging in

- It is done immediately after registration of a user.
- Login will return **Authentication credentials** in the callback,required for next login.
- After user disconnects from the server,user can again connect to server using room API, by calling it's login method. 

```java
    public class Main implements ConnectListener, AuthListener.LoginListener, AuthListener.RegisterListener {
    
        private LiveChatAPI liveChat;
        private LiveChatAPI.ChatRoom room; //This is required to provide abstraction over further communication
        private static String serverurl="wss://livechattest.rocket.chat/websocket";
    
    
        public void call(){
            liveChat=new LiveChatAPI(serverurl);
            liveChat.setReconnectionStrategy(null);
            liveChat.connect(this);
        }
    
        public static void main(String [] args){
            new Main().call();
        }
    
    
        @Override
        public void onConnect(String sessionID) {
            System.out.println("Connected to server");
            liveChat.registerGuest("saurabha","saurabha@gmail.com",null,this);
        }
    
        @Override
        public void onDisconnect(boolean closedByServer) {
            System.out.println("Disconnected from server");
        }
    
        @Override
        public void onConnectError(Exception websocketException) {
            System.out.println("Got connect error with the server");
        }
    
        @Override
        public void onRegister(GuestObject object, ErrorObject error) {
            if (error==null) {
                System.out.println("registration success");
                liveChat.login(object.getToken(), this);
            }else{
                System.out.println("error occurred "+error);
            }
        }
    
        @Override
        public void onLogin(GuestObject object, ErrorObject error) {
            if (error==null) {
                System.out.println("login is successful");
                room = liveChat.createRoom(object.getUserID(), object.getToken()); //Auth data is passed to room for further communication using room API.
    
            }else{
                System.out.println("error occurred "+error);
            }
        }
    }
```

- Till now we have used `LiveChatAPI` class, in order to use `LiveChatAPI.ChatRoom` class, you must login and pass
 appropriate credentials to `createRoom` method.
- In short, LiveChat SDK can be effectively used using two classes

1. LiveChatAPI
- Provides functionality that can hold instance required to maintain connection with server.
- API allows basic functionality like connect, login, register, reconnection and disconnection.

2. LiveChatAPI.ChatRoom
- It is created using LiveChatAPI and is extended version of basic API. 
- Provides abstraction as a **room**.
- All advanced API's like sending messages, loading history, getting agent data etc. can be used via ChatRoom.

#### 4. Further communication with server

- As room object is global accessible throughout Main class, it's method can be called from anywhere after initialization.
- Each method for communication with server is given in the [LiveChat Room API DOC]() .

#### 5. Handling disconnection with server



#### 6. Maintaining state of the room