package com.rocketchat.core.uploader;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.FileDescriptor;

/**
 * Created by sachin on 17/8/17.
 */
public class IFileUpload {

    public interface UfsCreateCallback extends Callback {
        void onUfsCreate(FileUploadToken token);
    }

    public interface UfsCompleteListener extends Callback {
        void onUfsComplete(FileDescriptor file);
    }
}
