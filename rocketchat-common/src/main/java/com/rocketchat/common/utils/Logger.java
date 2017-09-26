package com.rocketchat.common.utils;

public interface Logger {
    void info(String format, Object... args);

    void warning(String format, Object... args);

    void debug(String format, Object... args);
}
