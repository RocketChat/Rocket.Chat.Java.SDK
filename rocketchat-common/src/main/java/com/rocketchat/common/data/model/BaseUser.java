package com.rocketchat.common.data.model;

import com.squareup.moshi.Json;

import java.util.List;

import javax.annotation.Nullable;

public abstract class BaseUser {
    @Nullable @Json(name = "_id") public abstract String id();
    @Nullable public abstract String username();
    @Nullable public abstract List<String> roles();

    public abstract static class BaseBuilder<T extends BaseBuilder<T>> {
        public abstract T id(String id);
        public abstract T username(String username);
        public abstract T roles(List<String> roles);
    }

    public enum SortBy {
        USERNAME("username");

        private String propertyName;

        SortBy(String propertyName) {
            this.propertyName =  propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }

    public enum Status {
        @Json(name = "online") ONLINE,
        @Json(name = "busy") BUSY,
        @Json(name = "away") AWAY,
        @Json(name = "offline") OFFLINE
    }

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final String BUSY = "busy";
    public static final String AWAY = "away";

    public static Status getStatus(String s) {
        if (s.equals(ONLINE)) {
            return Status.ONLINE;
        } else if (s.equals(OFFLINE)) {
            return Status.OFFLINE;
        } else if (s.equals(BUSY)) {
            return Status.BUSY;
        } else if (s.equals(AWAY)) {
            return Status.AWAY;
        }
        return Status.OFFLINE;
    }
}
