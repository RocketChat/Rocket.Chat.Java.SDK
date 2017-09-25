package com.rocketchat.common.utils;

public interface Logger {
    void info(String format, String... args);

    void warning(String format, String... args);

    void debug(String format, String... args);
}
