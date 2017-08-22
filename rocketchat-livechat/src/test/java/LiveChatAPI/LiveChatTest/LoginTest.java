package LiveChatAPI.LiveChatTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import LiveChatAPI.LiveChatTest.LiveChatParent.ChatParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.livechat.LiveChatAPI;
import com.rocketchat.livechat.callback.AuthListener;
import com.rocketchat.livechat.callback.InitialDataListener;
import com.rocketchat.livechat.model.DepartmentObject;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatConfigObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */
public class LoginTest extends ChatParent implements ConnectListener, InitialDataListener, AuthListener.RegisterListener {

    LiveChatAPI.ChatRoom room;

    @Mock
    AuthListener.LoginListener listener;

    @Captor
    ArgumentCaptor<GuestObject> guestObjectArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorObjectArgumentCaptor;


    @Override
    public void setUpBefore() {
        super.setUpBefore();
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
            api.registerGuest("vijaya", "vijaya67@gmail.com", departmentId, this);
        }
    }

    @Override
    public void onRegister(GuestObject object, ErrorObject error) {
        api.login(object.getToken(), listener);
    }

    @Test
    public void loginTest() {
        Mockito.verify(listener, timeout(8000).atLeastOnce()).onLogin(guestObjectArgumentCaptor.capture(), errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue() == null);
        Assert.assertTrue(guestObjectArgumentCaptor.getValue() != null);
        GuestObject object = guestObjectArgumentCaptor.getValue();
        System.out.println("Login Object is " + object);
        room = api.createRoom(object.getUserID(), object.getToken());
    }

    @After
    public void closeConversation() {
        System.out.println("Closing conversation");
        room.closeConversation();
    }
}
