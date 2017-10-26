package com.rocketchat.core;

import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.data.CommonJsonAdapterFactory;
import com.rocketchat.common.data.TimestampAdapter;
import com.rocketchat.common.data.model.BaseRoom;
import com.rocketchat.common.listener.PaginatedCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.utils.NoopLogger;
import com.rocketchat.common.utils.Sort;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.JsonAdapterFactory;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.model.attachment.Attachment;
import com.rocketchat.core.provider.TokenProvider;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import io.fabric8.mockwebserver.DefaultMockServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RestImplTest {

    private RestImpl rest;
    private DefaultMockServer mockServer;

    @Mock private TokenProvider tokenProvider;
    @Mock private LoginCallback loginCallback;
    @Mock private PaginatedCallback<Attachment> paginatedCallback;
    @Mock private SimpleListCallback simpleListCallback;

    @Captor private ArgumentCaptor<Token> tokenCaptor;
    @Captor private ArgumentCaptor<List> listCaptor;
    @Captor private ArgumentCaptor<RocketChatException> exceptionCaptor;

    private static final int DEFAULT_TIMEOUT = 200;

    @Before
    public void setup() {
        mockServer = new DefaultMockServer();
        mockServer.start();

        HttpUrl baseUrl = HttpUrl.parse(mockServer.url("/"));
        OkHttpClient client = new OkHttpClient();

        Moshi moshi = new Moshi.Builder()
                .add(new TimestampAdapter())
                .add(JsonAdapterFactory.create())
                .add(CommonJsonAdapterFactory.create())
                .build();

        rest = new RestImpl(client, moshi, baseUrl, tokenProvider, new NoopLogger());
    }

    //     _____ _____ _____ _   _ _____ _   _    _______ ______  _____ _______ _____
    //    / ____|_   _/ ____| \ | |_   _| \ | |  |__   __|  ____|/ ____|__   __/ ____|
    //   | (___   | || |  __|  \| | | | |  \| |     | |  | |__  | (___    | | | (___
    //    \___ \  | || | |_ | . ` | | | | . ` |     | |  |  __|  \___ \   | |  \___ \
    //    ____) |_| || |__| | |\  |_| |_| |\  |     | |  | |____ ____) |  | |  ____) |
    //   |_____/|_____\_____|_| \_|_____|_| \_|     |_|  |______|_____/   |_| |_____/
    //
    //

    @Test(expected = NullPointerException.class)
    public void testSigninShouldFailWithNullUsername() {
        rest.signin(null, "password", loginCallback);
    }

    @Test(expected = NullPointerException.class)
    public void testSigninShouldFailWithNullPassword() {
        rest.signin("username", null, loginCallback);
    }

    @Test(expected = NullPointerException.class)
    public void testSigninShouldFailWithNullCallback() {
        rest.signin("username", "password", null);
    }

    @Test
    public void tesSigninShouldBeSuccessful() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/login")
                .andReturn(200, "{\"status\": \"success\",\"data\": {\"authToken\": \"token\",\"userId\": \"userid\"}}")
                .once();

        rest.signin("user", "password", loginCallback);

        verify(loginCallback, timeout(DEFAULT_TIMEOUT).only())
                .onLoginSuccess(tokenCaptor.capture());

        Token token = tokenCaptor.getValue();
        assertThat(token.getUserId(), is(equalTo("userid")));
        assertThat(token.getAuthToken(), is(equalTo("token")));
        assertThat(token.getExpiry(), is(nullValue()));
    }

    @Test
    public void testSigninShouldFailOnInvalidJson() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/login")
                .andReturn(200, "NOT A JSON")
                .once();

        rest.signin("user", "password", loginCallback);
        verify(loginCallback, timeout(DEFAULT_TIMEOUT).only())
                .onError(exceptionCaptor.capture());

        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception, is(instanceOf(RocketChatInvalidResponseException.class)));
        assertThat(exception.getMessage(), is(equalTo("A JSONObject text must begin with '{' at character 1")));
        assertThat(exception.getCause(), is(instanceOf(JSONException.class)));
    }

    @Test
    public void testSigninShouldFailWithAuthExceptionOn401() {
        mockServer.expect()
                .post()
                .withPath("/api/v1/login")
                .andReturn(401, "{\"status\": \"error\",\"message\": \"Unauthorized\"}")
                .once();

        rest.signin("user", "password", loginCallback);

        verify(loginCallback, timeout(DEFAULT_TIMEOUT).only())
                .onError(exceptionCaptor.capture());
        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception, is(instanceOf(RocketChatAuthException.class)));
        assertThat(exception.getMessage(), is(equalTo("Unauthorized")));
    }

    @Test
    public void testSigninShouldFailIfNot2xx() {
        rest.signin("user", "password", loginCallback);

        verify(loginCallback, timeout(DEFAULT_TIMEOUT).only())
                .onError(exceptionCaptor.capture());
        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception, is(instanceOf(RocketChatException.class)));

    }

    //     _____ ______ _______     _____   ____   ____  __  __        ______ _____ _      ______  _____   _______ ______  _____ _______ _____
    //    / ____|  ____|__   __|   |  __ \ / __ \ / __ \|  \/  |      |  ____|_   _| |    |  ____|/ ____| |__   __|  ____|/ ____|__   __/ ____|
    //   | |  __| |__     | |______| |__) | |  | | |  | | \  / |______| |__    | | | |    | |__  | (___      | |  | |__  | (___    | | | (___
    //   | | |_ |  __|    | |______|  _  /| |  | | |  | | |\/| |______|  __|   | | | |    |  __|  \___ \     | |  |  __|  \___ \   | |  \___ \
    //   | |__| | |____   | |      | | \ \| |__| | |__| | |  | |      | |     _| |_| |____| |____ ____) |    | |  | |____ ____) |  | |  ____) |
    //    \_____|______|  |_|      |_|  \_\\____/ \____/|_|  |_|      |_|    |_____|______|______|_____/     |_|  |______|_____/   |_| |_____/
    //
    //

    @Test(expected = NullPointerException.class)
    public void testGetRoomFilesShouldFailWithNullRoomId() {
        rest.getRoomFiles(null, BaseRoom.RoomType.PUBLIC, 0, Attachment.SortBy.UPLOADED_DATE, Sort.DESC, paginatedCallback);
    }

    @Test(expected = NullPointerException.class)
    public void testGetRoomFilesShouldFailWithNullRoomType() {
        rest.getRoomFiles("roomId", null, 0, Attachment.SortBy.UPLOADED_DATE, Sort.DESC, paginatedCallback);
    }

    @Test(expected = NullPointerException.class)
    public void testGetRoomFilesShouldFailWithNullSortBy() {
        rest.getRoomFiles("roomId", BaseRoom.RoomType.PUBLIC, 0, null, Sort.DESC, paginatedCallback);
    }

    @Test(expected = NullPointerException.class)
    public void testGetRoomFilesShouldFailWithNullSort() {
        rest.getRoomFiles("roomId", BaseRoom.RoomType.PUBLIC, 0, Attachment.SortBy.UPLOADED_DATE, null, paginatedCallback);
    }

    @Test(expected = NullPointerException.class)
    public void testGetRoomFilesShouldFailWithNullCallback() {
        rest.getRoomFiles("roomId", BaseRoom.RoomType.PUBLIC, 0, Attachment.SortBy.UPLOADED_DATE, Sort.DESC, null);
    }

    @Test
    public void testGetRoomFilesShouldFailOnInvalidJson() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.files")
                .andReturn(DEFAULT_TIMEOUT, "NOT A JSON")
                .once();

        rest.getRoomFiles("general", BaseRoom.RoomType.PUBLIC, 0, Attachment.SortBy.UPLOADED_DATE, Sort.DESC, paginatedCallback);
        verify(paginatedCallback, timeout(100).only())
                .onError(exceptionCaptor.capture());

        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception.getMessage(), is(equalTo("A JSONObject text must begin with '{' at character 0")));
        assertThat(exception.getCause(), is(instanceOf(JSONException.class)));
    }

    @Test
    public void testGetRoomFilesShouldBeSuccessful() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.files?roomId=general&offset=0&sort={%22uploadedAt%22:-1}")
                .andReturn(200,
                        "{\"total\":5000," +
                                "   \"offset\":0," +
                                "   \"success\":true," +
                                "   \"count\":1," +
                                "   \"files\":[" +
                                "      {" +
                                "         \"extension\":\"txt\"," +
                                "         \"description\":\"\"," +
                                "         \"store\":\"GoogleCloudStorage:Uploads\"," +
                                "         \"type\":\"text/plain\"," +
                                "         \"rid\":\"GENERAL\"," +
                                "         \"userId\":\"mTYz5v78fEETuyvxH\"," +
                                "         \"url\":\"/ufs/GoogleCloudStorage:Uploads/B5HXEJQvoqXjfMyKD/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3%20(2).txt\"," +
                                "         \"token\":\"C8BB59192B\"," +
                                "         \"path\":\"/ufs/GoogleCloudStorage:Uploads/B5HXEJQvoqXjfMyKD/%E6%96%B0%E5%BB%BA%E6%96%87%E6%9C%AC%E6%96%87%E6%A1%A3%20(2).txt\"," +
                                "         \"GoogleStorage\":{" +
                                "            \"path\":\"eoRXMCHBbQCdDnrke/uploads/GENERAL/mTYz5v78fEETuyvxH/B5HXEJQvoqXjfMyKD\"" +
                                "         }," +
                                "         \"instanceId\":\"kPnqzFNvmxkMWdcKC\"," +
                                "         \"size\":469," +
                                "         \"name\":\"sample.txt\"," +
                                "         \"progress\":1," +
                                "         \"uploadedAt\":\"2017-10-23T05:13:44.875Z\"," +
                                "         \"uploading\":false," +
                                "         \"etag\":\"mXWYhuiWiCxXpDYdg\"," +
                                "         \"_id\":\"B5HXEJQvoqXjfMyKD\"," +
                                "         \"complete\":true," +
                                "         \"_updatedAt\":\"2017-10-23T05:13:43.220Z\"" +
                                "      }" +
                                "   ]" +
                                "}")
                .once();

        rest.getRoomFiles("general", BaseRoom.RoomType.PUBLIC, 0, Attachment.SortBy.UPLOADED_DATE, Sort.DESC, paginatedCallback);

        verify(paginatedCallback, timeout(DEFAULT_TIMEOUT).only())
                .onSuccess(listCaptor.capture(), anyInt());

        List<Attachment> attachmentList = listCaptor.getValue();
        assertThat(attachmentList, is(notNullValue()));
        assertThat(attachmentList.size(), is(equalTo(1)));
        Attachment attachment = attachmentList.get(0);
        assertThat(attachment.getId(), is(equalTo("B5HXEJQvoqXjfMyKD")));
        assertThat(attachment.getName(), is(equalTo("sample.txt")));
    }

    //     _____ ______ _______     _    _  _____ ______ _____         _____ _____   ____  _    _ _____        _      _____  _____ _______   _______ ______  _____ _______ _____
    //    / ____|  ____|__   __|   | |  | |/ ____|  ____|  __ \       / ____|  __ \ / __ \| |  | |  __ \      | |    |_   _|/ ____|__   __| |__   __|  ____|/ ____|__   __/ ____|
    //   | |  __| |__     | |______| |  | | (___ | |__  | |__) |_____| |  __| |__) | |  | | |  | | |__) |_____| |      | | | (___    | |       | |  | |__  | (___    | | | (___
    //   | | |_ |  __|    | |______| |  | |\___ \|  __| |  _  /______| | |_ |  _  /| |  | | |  | |  ___/______| |      | |  \___ \   | |       | |  |  __|  \___ \   | |  \___ \
    //   | |__| | |____   | |      | |__| |____) | |____| | \ \      | |__| | | \ \| |__| | |__| | |          | |____ _| |_ ____) |  | |       | |  | |____ ____) |  | |  ____) |
    //    \_____|______|  |_|       \____/|_____/|______|_|  \_\      \_____|_|  \_\\____/ \____/|_|          |______|_____|_____/   |_|       |_|  |______|_____/   |_| |_____/
    //
    //

    @Test(expected = NullPointerException.class)
    public void testUserGroupListShouldFailWithNullCallback() {
        rest.getUserGroupList(null);
    }

    @Test
    public void testUserGroupListShouldBeSuccessful() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/groups.list")
                .andReturn(200, "{" +
                        "    \"groups\": [" +
                        "        {" +
                        "            \"_id\": \"ByehQjC44FwMeiLbX\"," +
                        "            \"name\": \"test-test\"," +
                        "            \"t\": \"p\"," +
                        "            \"usernames\": [" +
                        "                \"testing1\"" +
                        "            ]," +
                        "            \"msgs\": 0," +
                        "            \"u\": {" +
                        "                \"_id\": \"aobEdbYhXfu5hkeqG\"," +
                        "                \"username\": \"testing1\"" +
                        "            }," +
                        "            \"ts\": \"2016-12-09T15:08:58.042Z\"," +
                        "            \"ro\": false," +
                        "            \"sysMes\": true," +
                        "            \"_updatedAt\": \"2016-12-09T15:22:40.656Z\"" +
                        "        }," +
                        "        {" +
                        "            \"_id\": \"t7qapfhZjANMRAi5w\"," +
                        "            \"name\": \"testing\"," +
                        "            \"t\": \"p\"," +
                        "            \"usernames\": [" +
                        "                \"testing2\"" +
                        "            ]," +
                        "            \"msgs\": 0," +
                        "            \"u\": {" +
                        "                \"_id\": \"y65tAmHs93aDChMWu\"," +
                        "                \"username\": \"testing2\"" +
                        "            }," +
                        "            \"ts\": \"2016-12-01T15:08:58.042Z\"," +
                        "            \"ro\": false," +
                        "            \"sysMes\": true," +
                        "            \"_updatedAt\": \"2016-12-09T15:22:40.656Z\"" +
                        "        }" +
                        "    ]," +
                        "    \"success\": true" +
                        "}")
                .once();

        rest.getUserGroupList(simpleListCallback);

        verify(simpleListCallback, timeout(DEFAULT_TIMEOUT).only())
                .onSuccess(listCaptor.capture());

        List<Subscription> subscriptionList = listCaptor.getValue();
        assertThat(subscriptionList, is(notNullValue()));
        assertThat(subscriptionList.size(), is(equalTo(2)));
        Subscription subscription = subscriptionList.get(0);
        assertThat(subscription.roomId(), is(equalTo("ByehQjC44FwMeiLbX")));
        assertThat(subscription.name(), is(equalTo("test-test")));
        assertThat(subscription.type(), is(equalTo(BaseRoom.RoomType.PRIVATE)));
    }

    //     _____ ______ _______     _    _  _____ ______ _____         _____ _    _          _   _ _   _ ______ _          _      _____  _____ _______   _______ ______  _____ _______ _____
    //    / ____|  ____|__   __|   | |  | |/ ____|  ____|  __ \       / ____| |  | |   /\   | \ | | \ | |  ____| |        | |    |_   _|/ ____|__   __| |__   __|  ____|/ ____|__   __/ ____|
    //   | |  __| |__     | |______| |  | | (___ | |__  | |__) |_____| |    | |__| |  /  \  |  \| |  \| | |__  | |  ______| |      | | | (___    | |       | |  | |__  | (___    | | | (___
    //   | | |_ |  __|    | |______| |  | |\___ \|  __| |  _  /______| |    |  __  | / /\ \ | . ` | . ` |  __| | | |______| |      | |  \___ \   | |       | |  |  __|  \___ \   | |  \___ \
    //   | |__| | |____   | |      | |__| |____) | |____| | \ \      | |____| |  | |/ ____ \| |\  | |\  | |____| |____    | |____ _| |_ ____) |  | |       | |  | |____ ____) |  | |  ____) |
    //    \_____|______|  |_|       \____/|_____/|______|_|  \_\      \_____|_|  |_/_/    \_\_| \_|_| \_|______|______|   |______|_____|_____/   |_|       |_|  |______|_____/   |_| |_____/
    //
    //

    @Test(expected = NullPointerException.class)
    public void testUserChannelListShouldFailWithNullCallback() {
        rest.getUserChannelList(null);
    }

    @Test
    public void testUserChannelListShouldBeSuccessful() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/channels.list.joined")
                .andReturn(200, "{" +
                        "    \"channels\": [" +
                        "        {" +
                        "            \"_id\": \"ByehQjC44FwMeiLbX\"," +
                        "            \"name\": \"invite-me\"," +
                        "            \"t\": \"c\"," +
                        "            \"usernames\": [" +
                        "                \"testing1\"" +
                        "            ]," +
                        "            \"msgs\": 0," +
                        "            \"u\": {" +
                        "                \"_id\": \"aobEdbYhXfu5hkeqG\"," +
                        "                \"username\": \"testing1\"" +
                        "            }," +
                        "            \"ts\": \"2016-12-09T15:08:58.042Z\"," +
                        "            \"ro\": false," +
                        "            \"sysMes\": true," +
                        "            \"_updatedAt\": \"2016-12-09T15:22:40.656Z\"" +
                        "        }" +
                        "    ]," +
                        "    \"success\": true" +
                        "}")
                .once();

        rest.getUserChannelList(simpleListCallback);

        verify(simpleListCallback, timeout(DEFAULT_TIMEOUT).only())
                .onSuccess(listCaptor.capture());

        List<Subscription> subscriptionList = listCaptor.getValue();
        assertThat(subscriptionList, is(notNullValue()));
        assertThat(subscriptionList.size(), is(equalTo(1)));
        Subscription subscription = subscriptionList.get(0);
        assertThat(subscription.roomId(), is(equalTo("ByehQjC44FwMeiLbX")));
        assertThat(subscription.name(), is(equalTo("invite-me")));
        assertThat(subscription.type(), is(equalTo(BaseRoom.RoomType.PUBLIC)));
    }

    //     _____ ______ _______     _    _  _____ ______ _____        _____ _____ _____  ______ _____ _______     __  __ ______  _____ _____         _____ ______      _      _____  _____ _______   _______ ______  _____ _______ _____
    //    / ____|  ____|__   __|   | |  | |/ ____|  ____|  __ \      |  __ \_   _|  __ \|  ____/ ____|__   __|   |  \/  |  ____|/ ____/ ____|  /\   / ____|  ____|    | |    |_   _|/ ____|__   __| |__   __|  ____|/ ____|__   __/ ____|
    //   | |  __| |__     | |______| |  | | (___ | |__  | |__) |_____| |  | || | | |__) | |__ | |       | |______| \  / | |__  | (___| (___   /  \ | |  __| |__ ______| |      | | | (___    | |       | |  | |__  | (___    | | | (___
    //   | | |_ |  __|    | |______| |  | |\___ \|  __| |  _  /______| |  | || | |  _  /|  __|| |       | |______| |\/| |  __|  \___ \\___ \ / /\ \| | |_ |  __|______| |      | |  \___ \   | |       | |  |  __|  \___ \   | |  \___ \
    //   | |__| | |____   | |      | |__| |____) | |____| | \ \      | |__| || |_| | \ \| |___| |____   | |      | |  | | |____ ____) |___) / ____ \ |__| | |____     | |____ _| |_ ____) |  | |       | |  | |____ ____) |  | |  ____) |
    //    \_____|______|  |_|       \____/|_____/|______|_|  \_\     |_____/_____|_|  \_\______\_____|  |_|      |_|  |_|______|_____/_____/_/    \_\_____|______|    |______|_____|_____/   |_|       |_|  |______|_____/   |_| |_____/
    //
    //

    @Test(expected = NullPointerException.class)
    public void testUserDirectMessagelListShouldFailWithNullCallback() {
        rest.getUserDirectMessageList(null);
    }

    @Test
    public void testUserDirectMessagelListShouldBeSuccessful() {
        mockServer.expect()
                .get()
                .withPath("/api/v1/dm.list")
                .andReturn(200, "{" +
                        "   \"success\":true," +
                        "   \"ims\":[" +
                        "      {" +
                        "         \"msgs\":90," +
                        "         \"lm\":\"2017-09-29T14:43:54.207Z\"," +
                        "         \"t\":\"d\"," +
                        "         \"usernames\":[" +
                        "            \"user1\"," +
                        "            \"user2\"" +
                        "         ]," +
                        "         \"_id\":\"0WCaFa2Jve4FEjMYacBD6dHcSoBvGjkrzM\"," +
                        "         \"_updatedAt\":\"2017-09-29T14:43:54.286Z\"," +
                        "         \"ts\":\"2017-08-15T17:56:09.013Z\"" +
                        "      }," +
                        "      {" +
                        "         \"msgs\":2," +
                        "         \"lm\":\"2017-08-18T18:01:33.580Z\"," +
                        "         \"t\":\"d\"," +
                        "         \"usernames\":[" +
                        "            \"user1\"," +
                        "            \"user3\"" +
                        "         ]," +
                        "         \"_id\":\"0WCaFa2Jve4FEjMYacBD6dHcIoBEGjJszO\"," +
                        "         \"_updatedAt\":\"2017-08-18T18:01:33.667Z\"," +
                        "         \"ts\":\"2017-08-18T18:00:57.369Z\"" +
                        "      }" +
                        "   ]" +
                        "}")
                .once();

        rest.getUserDirectMessageList(simpleListCallback);

        verify(simpleListCallback, timeout(DEFAULT_TIMEOUT).only())
                .onSuccess(listCaptor.capture());

        List<Subscription> subscriptionList = listCaptor.getValue();
        assertThat(subscriptionList, is(notNullValue()));
        assertThat(subscriptionList.size(), is(equalTo(2)));
        Subscription subscription = subscriptionList.get(0);
        assertThat(subscription.roomId(), is(equalTo("0WCaFa2Jve4FEjMYacBD6dHcSoBvGjkrzM")));
        assertThat(subscription.type(), is(equalTo(BaseRoom.RoomType.ONE_TO_ONE)));
    }
}