package RocketChatAPI.RocketChatRoomTest;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.core.model.SubscriptionObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommonRoomTest extends RoomParent {

    public static String leavedRoomId = "";
    @Rule
    public TestName testName = new TestName();
    @Mock
    SimpleListener listener;
    @Captor
    ArgumentCaptor<Boolean> successCaptor;
    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        super.onGetSubscriptions(subscriptions, error);
        if (testName.getMethodName().equals("A_archiveTest")) {
            room.archive(listener);
        } else if (testName.getMethodName().equals("B_unarchiveTest")) {
            room.unarchive(listener);
        } else if (testName.getMethodName().equals("C_hideTest")) {
            room.hide(listener);
        } else if (testName.getMethodName().equals("D_openTest")) {
            room.open(listener);
        } else if (testName.getMethodName().equals("E_setFavouriteRoomTest")) {
            room.setFavourite(true, listener);
        } else if (testName.getMethodName().equals("F_leaveGroup")) {
            room = api.getChatRoomFactory().getChatRoomByName("general");
            if (room == null) {
                try {
                    throw new Exception("Room is null, can't proceed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                leavedRoomId = room.getRoomData().getRoomId();
                System.out.println("leaved room id is " + leavedRoomId);
                room.leave(listener);
            }
        } else if (testName.getMethodName().equals("G_joinGroup")) {
            api.joinPublicGroup(leavedRoomId, null, listener);
        }

    }

    public void TestThisCode() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).callback(successCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(successCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
    }

    @Test
    public void A_archiveTest() {
        TestThisCode();
    }

    @Test
    public void B_unarchiveTest() {
        TestThisCode();
    }

    @Test
    public void C_hideTest() {
        TestThisCode();
    }

    @Test
    public void D_openTest() {
        TestThisCode();
    }

    @Test
    public void E_setFavouriteRoomTest() {
        TestThisCode();
    }

    @Test
    public void F_leaveGroup() {
        TestThisCode();
    }

    @Test
    public void G_joinGroup() {
        TestThisCode();
    }

}
