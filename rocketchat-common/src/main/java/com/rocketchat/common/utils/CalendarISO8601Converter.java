package com.rocketchat.common.utils;

import com.rocketchat.common.data.ISO8601Converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarISO8601Converter implements ISO8601Converter {

    @Override
    public String fromTimestamp(long timestamp) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTimeInMillis(timestamp);

        // Quoted "Z" to indicate UTC, no timezone offset
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);

        return df.format(calendar.getTime());
    }

    @Override
    public long toTimestamp(String date) throws ParseException {
        TimeZone tz = TimeZone.getTimeZone("UTC");

        // Quoted "Z" to indicate UTC, no timezone offset
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.parse(date).getTime();
    }
}
