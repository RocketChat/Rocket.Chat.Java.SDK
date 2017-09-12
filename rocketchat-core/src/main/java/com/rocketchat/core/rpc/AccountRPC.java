package com.rocketchat.core.rpc;

import com.rocketchat.common.data.rpc.RPC;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 24/7/17.
 */
public class AccountRPC extends RPC {

    private static final String PUBLIC_SETTINGS = "public-settings/get";

    private static final String GET_PERMISSIONS = "permissions/get";

    public static String getPublicSettings(int integer, Date date) {
        return getCommonData(integer, PUBLIC_SETTINGS, date);
    }

    public static String getPermissions(int integer, Date date) {
        return getCommonData(integer, GET_PERMISSIONS, date);
    }

    private static String getCommonData(int integer, String methodName, Date date) {
        if (date == null) {
            return getRemoteMethodObject(integer, methodName).toString();
        } else {

            JSONObject dt = new JSONObject();
            try {
                dt.put("$date", date.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getRemoteMethodObject(integer, methodName, dt).toString();
        }
    }
}
