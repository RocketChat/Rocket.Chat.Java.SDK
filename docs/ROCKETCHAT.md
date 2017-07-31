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
- Login can be done in username and password as well as using Token for next time Login.
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

public class Main implements ConnectListener, LoginListener {

```
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
        api.loginUsingToken("ju-c1BRuPmcUhKSFgLPoh9L6bhyEhHCrdMuX9NlKAe3",this);
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
- 
- 


#### 4. Creating logical rooms for communication with server
- 
- 

#### 5. Log out