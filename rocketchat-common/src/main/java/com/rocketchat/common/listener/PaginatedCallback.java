package com.rocketchat.common.listener;

import java.util.List;

public interface PaginatedCallback<T> extends Callback {
    void onSuccess(List<T> list, long total, long offset);
}
