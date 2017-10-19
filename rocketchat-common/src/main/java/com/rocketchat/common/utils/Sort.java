package com.rocketchat.common.utils;

/**
 * Enum class that specifies the order in which the results should be returned.
 *
 * @author Filipe de Lima Brito (filipedelimabrito@gmail.com)
 * @since 0.8.0
 */
public enum Sort {
    ASC("1"),
    DESC("-1");

    private String direction;

    Sort(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}