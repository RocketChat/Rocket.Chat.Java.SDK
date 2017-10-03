package com.rocketchat.common.utils;

public class NoopLogger implements Logger {
    @Override
    public void info(String format, Object... args) {

    }

    @Override
    public void warning(String format, Object... args) {

    }

    @Override
    public void debug(String format, Object... args) {

    }
}
