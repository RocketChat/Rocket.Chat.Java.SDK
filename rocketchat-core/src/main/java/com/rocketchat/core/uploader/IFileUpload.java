package com.rocketchat.core.uploader;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.FileObject;

/**
 * Created by sachin on 17/8/17.
 */
public class IFileUpload {

    public interface UfsCreateCallback extends Callback {
        void onUfsCreate(FileUploadToken token);
    }

    public interface UfsCompleteListener extends Callback {
        void onUfsComplete(FileObject file);
    }
}
