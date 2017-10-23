package com.rocketchat.common.data;

import java.text.ParseException;

public interface ISO8601Converter {
    String fromTimestamp(long timestamp);

    long toTimestamp(String date) throws ParseException;
}
