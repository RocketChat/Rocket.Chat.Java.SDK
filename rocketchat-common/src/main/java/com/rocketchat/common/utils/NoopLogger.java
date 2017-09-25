package com.rocketchat.common.utils;

public class NoopLogger implements Logger {
    @Override
    public void info(String format, String... args) {

    }

    @Override
    public void warning(String format, String... args) {

    }

    @Override
    public void debug(String format, String... args) {

    }
}
