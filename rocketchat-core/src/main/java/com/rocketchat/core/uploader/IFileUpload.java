package com.rocketchat.core.uploader;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.FileObject;

import java.lang.reflect.Type;

/**
 * Created by sachin on 17/8/17.
 */
public class IFileUpload {

    public static abstract class UfsCreateCallback extends Callback {
        public abstract void onUfsCreate(FileUploadToken token);

        @Override
        public Type getClassType() {
            return UfsCreateCallback.class;
        }
    }

    public static abstract class UfsCompleteListener extends Callback {
        public abstract void onUfsComplete(FileObject file);

        @Override
        public Type getClassType() {
            return UfsCompleteListener.class;
        }
    }
}
