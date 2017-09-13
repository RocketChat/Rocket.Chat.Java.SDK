package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.core.RocketChatAPI;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CompletableFuture;

/**
 * Created by sachin on 3/8/17.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommonRoomTest extends RoomParent {

    public static String leavedRoomId = "";
    @Rule
    public TestName testName = new TestName();

    public void TestThisCode() throws Exception {
        CompletableFuture<RocketChatAPI.ChatRoom> roomResult = getChatRoom();
        Boolean result = roomResult.thenCompose(room -> {
            if (testName.getMethodName().equals("A_archiveTest")) {
                return room.archive();
            } else if (testName.getMethodName().equals("B_unarchiveTest")) {
                return room.unarchive();
            } else if (testName.getMethodName().equals("C_hideTest")) {
                return room.hide();
            } else if (testName.getMethodName().equals("D_openTest")) {
                return room.open();
            } else if (testName.getMethodName().equals("E_setFavouriteRoomTest")) {
                return room.setFavourite(true);
            } else if (testName.getMethodName().equals("F_leaveGroup")) {
                room = api.getChatRoomFactory().getChatRoomByName("general");
                if (room == null) {
                    throw new IllegalStateException("Room is null, can't proceed");
                } else {
                    leavedRoomId = room.getRoomData().getRoomId();
                    System.out.println("leaved room id is " + leavedRoomId);
                    return room.leave();
                }
            } else if (testName.getMethodName().equals("G_joinGroup")) {
                return api.joinPublicGroup(leavedRoomId, null);
            } else {
                throw new IllegalStateException();
            }
        }).get();
        Assert.assertNotNull(result);
    }

    @Test(timeout = 12000)
    public void A_archiveTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void B_unarchiveTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void C_hideTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void D_openTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void E_setFavouriteRoomTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void F_leaveGroup() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void G_joinGroup() throws Exception {
        TestThisCode();
    }

}
