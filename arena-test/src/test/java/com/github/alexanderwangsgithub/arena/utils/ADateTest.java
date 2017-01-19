package com.github.alexanderwangsgithub.arena.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wanggang on 8/16/16.
 */
public class ADateTest {
    @Test
    public void getSecondsTest() {
        System.out.println("now seconds : " + ADate.getSeconds(LocalDateTime.now()));
    }

    @Test
    public void toLocalDateTimeTest() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Assert.assertEquals(ADate.toLocalDateTime(new Date()).getDayOfMonth(), cal.get(Calendar.DATE));
    }

    @Test
    public void test() {
        String timeStr = "2016";
        System.out.println(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        System.out.println(cal.get(Calendar.DATE));
        System.out.println(ADate.toLocalDateTime(new Date()).getDayOfMonth());
        Assert.assertEquals(ADate.toLocalDateTime(new Date()).getDayOfMonth(), cal.get(Calendar.DATE));


        cal.add(Calendar.DATE, 14);
        System.out.println(cal.get(Calendar.DATE));
        cal.add(Calendar.DATE, 1);
        System.out.println(cal.get(Calendar.DATE));

        System.out.println(ADate.toLocalDateTime(new Date()).plusDays(1));

        System.out.println(LocalDate.now().atStartOfDay(ZoneId.systemDefault()));
        System.out.println(LocalTime.now());

        Assert.assertEquals(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toLocalDateTime(), LocalDate.now().atStartOfDay());

        System.out.println(ADate.getSeconds(LocalDateTime.now()));
        System.out.println(ADate.getSeconds(ADate.getEndOfLocalDateTime(LocalDateTime.now())));
        System.out.println(LocalDateTime.now().getMonthValue());

    }
}
