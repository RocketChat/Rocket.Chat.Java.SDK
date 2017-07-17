package LiveChatAPI.LiveChatTest;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.AuthListener;
import io.rocketchat.livechat.callback.ConnectListener;
import io.rocketchat.livechat.callback.InitialDataListener;
import io.rocketchat.livechat.model.DepartmentObject;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.LiveChatConfigObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.ArrayList;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */
public class RegisterTest implements ConnectListener, InitialDataListener {
    private static String serverurl="wss://livechattest.rocket.chat/websocket";

    LiveChatAPI api;

    @Mock
    AuthListener.RegisterListener listener;

    @Captor
    ArgumentCaptor <GuestObject> guestObjectArgumentCaptor;

    @Captor
    ArgumentCaptor <ErrorObject> errorObjectArgumentCaptor;

    @Before
    public void setUpBefore(){
        MockitoAnnotations.initMocks( this );
        System.out.println("before got called");
        api= new LiveChatAPI(serverurl);
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
        String departmentId=null;
        if (error==null){
            ArrayList <DepartmentObject> departmentObjects=object.getDepartments();
            if (departmentObjects.size()>0){
                departmentId=departmentObjects.get(0).getId();
            }
            api.registerGuest("vishal","vishal34@gmail.com",departmentId,listener);
        }
    }

    @Test
    public void registerTest(){
        Mockito.verify(listener, timeout(6000).atLeastOnce()).onRegister(guestObjectArgumentCaptor.capture(),errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue() == null);
        Assert.assertTrue(guestObjectArgumentCaptor != null);
        System.out.println("Register Object is " + guestObjectArgumentCaptor.getValue());
    }
}
