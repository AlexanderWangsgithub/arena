package com.github.alexanderwangsgithub.arena.utils;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wanggang on 8/16/16.
 */
public class DateUtils {

    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        return toLocalDateTime(calendar.getTime());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp * 1000), ZoneId.systemDefault());
    }

    public static long getSeconds(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime getStartOfLocalDateTime(LocalDateTime localDateTime) {

        return localDateTime.withHour(0).withMinute(0).withSecond(0);
    }

    public static LocalDateTime getEndOfLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59);
    }
}
