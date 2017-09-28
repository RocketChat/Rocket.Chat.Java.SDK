package com.rocketchat.core.internal.rpc;

import com.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 17/8/17.
 */
public class FileUploadRPC extends RPC {

    private static final String UFS_CREATE = "ufsCreate";
    private static final String UFS_COMPLETE = "ufsComplete";

    public static String ufsCreate(int integer, String fileName, int fileSize, String fileType, String roomId, String description, String store) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", fileName);
            object.put("size", fileSize);
            object.put("type", fileType);
            object.put("rid", roomId);
            object.put("description", description);
            object.put("store", store);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer, UFS_CREATE, object).toString();
    }

    public static String ufsComplete(int integer, String fileId, String store, String token) {
        return getRemoteMethodObject(integer, UFS_COMPLETE, fileId, store, token).toString();
    }
}
