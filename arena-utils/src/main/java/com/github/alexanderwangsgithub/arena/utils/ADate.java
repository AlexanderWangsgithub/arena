package com.github.alexanderwangsgithub.arena.utils;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by wanggang on 8/16/16.
 */
public final class ADate {

    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        return toLocalDateTime(calendar.getTime());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /*long转LocalDateTime*/
    public static LocalDateTime toLocalDateTime(long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp * 1000), ZoneId.systemDefault());
    }

    /*转为long，秒级*/
    public static long getSeconds(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    /*转为long，毫秒级*/
    public static long toLong(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /*获得一天的开始*/
    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(0).withMinute(0).withSecond(0);
    }

    /*获得一天的结束*/
    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        return localDateTime.withHour(23).withMinute(59).withSecond(59);
    }



    /*获得给定范围的随机时间*/
    public static LocalDateTime dateRandom(LocalDateTime start, LocalDateTime end){
        long startL= toLong(start);
        long endL= toLong(end);
        int period = Long.valueOf(endL - startL).intValue();
        Random random = new Random();
        int add = random.nextInt(period);
        startL+=add;
        return toLocalDateTime(startL);
    }
}
