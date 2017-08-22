package LiveChatAPI.LiveChatRoomTest.ChatRoomParent;

import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.livechat.LiveChatAPI;
import com.rocketchat.livechat.callback.AuthListener;
import com.rocketchat.livechat.callback.InitialDataListener;
import com.rocketchat.livechat.model.DepartmentObject;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatConfigObject;

/**
 * Created by sachin on 17/7/17.
 */
public class RoomParent implements ConnectListener, InitialDataListener, AuthListener.RegisterListener, AuthListener.LoginListener {

    private static String serverurl = "wss://livechattest.rocket.chat/websocket";
    public LiveChatAPI.ChatRoom room;
    LiveChatAPI api;

    public void setUpBefore() {
        MockitoAnnotations.initMocks(this);
        System.out.println("before got called");
        api = new LiveChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.getInitialData(this);
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Connect error to server");
    }

    @Override
    public void onInitialData(LiveChatConfigObject object, ErrorObject error) {
        String departmentId = null;
        if (error == null) {
            ArrayList<DepartmentObject> departmentObjects = object.getDepartments();
            if (departmentObjects.size() > 0) {
                departmentId = departmentObjects.get(0).getId();
            }
            api.registerGuest("aditi", "aditi89@gmail.com", departmentId, this);
        }
    }

    @Override
    public void onRegister(GuestObject object, ErrorObject error) {
        api.login(object.getToken(), this);
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        room = api.createRoom(object.getUserID(), object.getToken());
    }

    public void closeConversation() {
        room.closeConversation();
    }
}
