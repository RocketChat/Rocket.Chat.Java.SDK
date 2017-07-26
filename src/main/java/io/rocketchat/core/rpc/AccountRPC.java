package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 24/7/17.
 */
// TODO: 24/7/17 Implement AccountRPC required for signup
public class AccountRPC extends RPC{

    public static String PUBLICSETTINGS="public-settings/get";

    public static String GETPERMISSIONS="permissions/get";

    public static String getPublicSettings(int integer, Date date){
        return getCommonData(integer,PUBLICSETTINGS,date);
    }

    public static String getPermissions(int integer,Date date){
        return getCommonData(integer,GETPERMISSIONS,date);
    }

    public static String getCommonData(int integer, String methodName, Date date){
        if (date==null) {
            return getRemoteMethodObject(integer, methodName).toString();
        }else{

            JSONObject dt=new JSONObject();
            try {
                dt.put("$date",date.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getRemoteMethodObject(integer, methodName,dt).toString();
        }
    }
}
