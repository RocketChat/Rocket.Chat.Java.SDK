package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.core.callback.EmojiListener;
import com.rocketchat.core.model.Emoji;
import com.rocketchat.core.model.TokenObject;

import java.util.ArrayList;

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
 * Created by sachin on 3/8/17.
 */
public class ListCustomEmojiTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    @Mock
    EmojiListener listener;

    @Captor
    ArgumentCaptor<ArrayList<Emoji>> listArgumentCaptor;
    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    @Before
    public void setUp() {
        super.setUpBefore(true);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected successfully");
        api.login(username, password, this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.listCustomEmoji(listener);
    }

    @Test
    public void listCustomEmojiTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onListCustomEmoji(listArgumentCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(listArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        Assert.assertTrue(listArgumentCaptor.getValue().size() > 0);
    }

    @After
    public void logout() {
        api.logout(null);
    }
}
