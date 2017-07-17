package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.callback.AuthListener;
import io.rocketchat.livechat.model.GuestObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */

public class LoginTest extends RoomParent {


    @Mock
    AuthListener.LoginListener loginListener;

    @Captor
    ArgumentCaptor <GuestObject> guestObjectArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorObjectArgumentCaptor;

    @Before
    public void setup(){
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
        room.login(loginListener);
    }

    @Test
    public void loginTest(){
        Mockito.verify(loginListener, timeout(8000).atLeastOnce()).onLogin(guestObjectArgumentCaptor.capture(),errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue()== null);
        Assert.assertNotNull(guestObjectArgumentCaptor.getValue());
        System.out.println("Logged in with user id "+guestObjectArgumentCaptor.getValue().getUserID());
    }

    @After
    public void closeTest(){
        closeConversation();
    }
}
