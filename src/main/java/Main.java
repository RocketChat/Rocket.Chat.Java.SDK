import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.TokenObject;
import io.rocketchat.core.rpc.PresenceRPC;

import java.util.List;

/**
 * Created by sachin on 7/6/17.
 */

public class Main extends CoreAdapter {

    private static String serverurl = "wss://demo.rocket.chat/websocket";
    RocketChatAPI api;

    public static void main(String[] args) {
        new Main().call();
    }

    public void call() {
        api = new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.setPingInterval(3000);
        api.connect(this);

    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login("testuserrocks","testuserrocks",this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getRooms(this);
        api.setStatus(PresenceRPC.Status.OFFLINE);
    }

    @Override
    public void onGetRooms(List<RoomObject> rooms, ErrorObject error) {
        RocketChatAPI.ChatRoom room = api.getFactory().createChatRooms(rooms).getChatRoomByName("general");
        room.getMembers(false,this);
    }

    @Override
    public void onGetRoomMembers(Integer total, List<UserObject> members, ErrorObject error) {
        System.out.println("Total are "+total);
        System.out.println("Size is "+members.size());

    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnect detected");
    }
}

/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

//Bugs
// TODO: 29/7/17 Room created with a roomName, deleted again and created with same name mess up everything
