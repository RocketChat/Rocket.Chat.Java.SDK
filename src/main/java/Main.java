import io.rocketchat.common.network.ReconnectionStrategy;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;

/**
 * Created by sachin on 7/6/17.
 */

public class Main extends CoreAdapter{


    RocketChatAPI api;
    private static String serverurl="wss://demo.rocket.chat/websocket";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(new ReconnectionStrategy(10,2000));
        api.setPingInterval(3000);
        api.connect(this);

    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
    }


}


/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

//Bugs
// TODO: 29/7/17 Room created with a roomName, deleted again and created with same name mess up everything
