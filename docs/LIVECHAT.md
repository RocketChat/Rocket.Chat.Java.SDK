### Important Note :
- Before reading the documentation, go through all terms associated with LiveChat (Agent, Departments etc).
- Docs can be found here [LIVECHAT USER DOC](https://rocket.chat/docs/administrator-guides/livechat/).
- Make sure **LiveChat is properly configured** on the server.

### Overview

- Primary requirement is to have **URL** of hosted server.
- The process of connecting to server and registration must be done by following below steps

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


#### 2. Getting configuration data from server

- It is used to get **initial LiveChat configuration** from server.
- It will consist conf. data like departments, number of agents available, offline form, registration success message etc.

```java
    public class Main implements ConnectListener,  InitialDataListener{
    
        private LiveChatAPI liveChat;
        private LiveChatAPI.ChatRoom room; //This is required to provide abstraction over further communication
        private static String serverurl="wss://livechattest.rocket.chat/websocket";
    
    
        public void call(){
            liveChat=new LiveChatAPI(serverurl);
            liveChat.setReconnectionStrategy(new ReconnectionStrategy(10,5000));
            liveChat.connect(this);
        }
    
        public static void main(String [] args){
            new Main().call();
        }
    
    
        @Override
        public void onConnect(String sessionID) {
            System.out.println("Connected to server");
            liveChat.getInitialData(this);
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
        public void onInitialData(LiveChatConfigObject object, ErrorObject error) {
            System.out.println("Got initial data " + object);
            ArrayList <DepartmentObject> departmentObjects=object.getDepartments();
            if (departmentObjects.size()==0){
                    System.out.println("No departments available");
            }else{
                    System.out.println("Departments available "+departmentObjects);
            }
            
        }
    }
```
#### 3. Registration of a user

- Registration is a one time process done after getting initial data (as it will consist of data for departments).
- It is done in order to communicate with agent using **email** and **password**. 
- If departments are available, pass third parameter as **DepartmentId**.
- If no departments are available, pass third parameter as null.

```java 

    public class Main implements ConnectListener ,AuthListener.RegisterListener ,InitialDataListener{
    
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
            liveChat.getInitialData(this);
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
        public void onInitialData(LiveChatConfigObject object, ErrorObject error) {
            System.out.println("Got initial data " + object);
    
            ArrayList <DepartmentObject> departmentObjects=object.getDepartments();
            if (departmentObjects.size()==0){
                System.out.println("No departments available");
                liveChat.registerGuest("aditi","aditi@gmail.com",null,this);
               
            }else{
                System.out.println("Departments available "+departmentObjects);
                //Getting DepartmentId of first department
                            
                String departmentId=departmentObjects.get(0).getId();
                    
                liveChat.registerGuest("aditi","aditi@gmail.com",departmentId,this);
            }
            
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

#### 4. Logging in

- It is done immediately after registration of a user.
- Login will return **Guest Object (Authentication credentials)** in the callback, required for next login.
- User can re-login to server by using room API. 

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

- Till now we have used `LiveChatAPI` class. In order to use `LiveChatAPI.ChatRoom` class, you must login and pass
 appropriate credentials to `createRoom` method. `room` is used for further communication with server.

        * LiveChatAPI Class
        - Provides functionality that can hold instance required to maintain connection with server.
        - API allows basic functionality like connect, getting initial data, login, register, reconnection and disconnection.
        
        * LiveChatAPI.ChatRoom (room) Class
        - It is created using LiveChatAPI and is extended version of basic API. 
        - Provides abstraction as a real-world "chat-room".
        - All advanced API's like sending messages, loading history, getting agent data etc. can be used via ChatRoom.
        - It is possible to save state of a room in a file or database in the form of string.
        - So, it is possible to use room API for further login and communication with server (login method from `LiveChatAPI` can be avoided after first time login).

#### 5. Further communication with server

- As room object is global accessible throughout Main class, it's methods can be called from anywhere after initialization.
- Each method for communication with server is given in the [LiveChat Room API DOC](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/master/LIVECHATDOC.md) .

#### 6. Handling re-connection with server
- `reconnect` method in `LiveChatAPI` class can be used for reconnecting to the server.

I. Manual reconnection
- Set reconnection to null before connecting to server.  

```java
    liveChat.setReconnectionStrategy(null);
    liveChat.connect(this);
```
- After disconnect event, reconnect to the server

```java
    @Override
    public void onDisconnect(boolean closedByServer) {
            liveChat.reconnect();
            System.out.println("Disconnected from server");
    }

```

II. Automatic reconnection
- Pass reconnection object while setting reconnection strategy

```java
        int maxAttempts=10;    //Number of attemps are 10
        int timeInterval=5000; // in milliseconds, reconnection will be called after 5 seconds
        liveChat.setReconnectionStrategy(new ReconnectionStrategy(maxAttempts,timeInterval));  
```

#### 7. Maintaining state of the room
- Maintaining state means even if `room` object is destroyed, it can be reconstructed.
- Whenever `room` object is created for the first time after login, call toString() method to get it's state.
- Save this state in file or database (permanent storage), next time read the file and pass this string to `room` the constructor.

I. Writing state to the file </br>
Example : </br>
- Suppose saveToFile is a function that saves string to the given fileName.

```java
    String state=room.toString();
    saveToFile("filename.txt",state);

```

II. Reading state from the file </br>
Example : </br>
- Suppose getFromFile is a function that returns String from the given file.
        
Example:
```java
    String state=getFromFile("filename.txt");
    /**
    **  liveChat is a initialized object connected to server  
    **/
    room=liveChat.new ChatRoom(state); // Constructor is used to construct room Object
    /**
    **  Login and other methods can be called by room
    **/
```

Use-case Samples
----------------
- Library is currently being used for development of [Rocket.Chat Android SDK](https://github.com/RocketChat/Rocket.Chat.Android.SDK) 
- For more information (important library classes), checkout library use [here ](https://github.com/RocketChat/Rocket.Chat.Android.SDK/tree/develop/rocketchatsdk/src/main/java/com/github/rocketchat/livechat) 
