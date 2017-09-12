package com.rocketchat.common.listener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sachin on 26/7/17.
 */
public abstract class SimpleListCallback<T> extends Callback {

    private Type type;

    public abstract void onSuccess(List<T> list);

    @Override
    public Type getClassType() {
        if (type == null) {
            type = getClass().getGenericSuperclass();
        }

        return type;
    }
}
