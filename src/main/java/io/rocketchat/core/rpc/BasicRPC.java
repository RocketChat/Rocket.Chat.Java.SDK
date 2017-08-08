package io.rocketchat.core.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.utils.Utils;

/**
 * Created by sachin on 8/6/17.
 */

public class BasicRPC extends RPC {

    private static String LOGIN = "login";
    private static String GET_USER_ROLES = "getUserRoles";
    private static String GET_SUBSCRIPTIONS = "subscriptions/get";
    private static String GET_ROOMS = "rooms/get";
    private static String GET_ROOM_ROLES = "getRoomRoles";
    private static String LIST_EMOJI = "listEmojiCustom";
    private static String LOGOUT = "logout";

    /**
     * Tested
     */
    public static String login(int integer, String username, String password) {

        JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("user", new JSONObject().put("username", username));
            loginObject.put("password", new JSONObject().put("digest", Utils.getDigest(password)).put("algorithm", "sha-256"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer, LOGIN, loginObject).toString();
    }

    /**
     * Tested
     */
    public static String loginUsingToken(int integer, String token) {
        JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("resume", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer, LOGIN, loginObject).toString();
    }

    /**
     * Tested
     */

    public static String getUserRoles(int integer) {
        return getRemoteMethodObject(integer, GET_USER_ROLES).toString();
    }

    /**
     * Tested
     */

    // TODO: 29/7/17 add getSubscriptions based on date
    public static String getSubscriptions(int integer) {
        return getRemoteMethodObject(integer, GET_SUBSCRIPTIONS).toString();
    }

    /**
     * Tested
     */
    // TODO: 29/7/17 add getRooms based on date
    public static String getRooms(int integer) {
        return getRemoteMethodObject(integer, GET_ROOMS).toString();
    }

    /**
     * Used to return users with room roles
     *
     * @param integer
     * @param roomId  List of comma separated room Id to return room specific roles
     * @return
     */
    public static String getRoomRoles(int integer, String... roomId) {
        return getRemoteMethodObject(integer, GET_ROOM_ROLES, (Object[]) roomId).toString();
    }

    /**
     * Returns a list of custom emoji registered with the server. There’s no need for parameters.
     *
     * @param integer
     * @return
     */
    public static String listCustomEmoji(int integer) {
        return getRemoteMethodObject(integer, LIST_EMOJI).toString();
    }

    /**
     * Used to logout from server
     *
     * @param integer
     * @return
     */
    public static String logout(int integer) {
        return getRemoteMethodObject(integer, LOGOUT).toString();
    }

}
