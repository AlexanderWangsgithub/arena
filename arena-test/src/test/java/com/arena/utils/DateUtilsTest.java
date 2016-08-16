package com.arena.utils;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

/**
 * Created by wanggang on 8/16/16.
 */
public class DateUtilsTest {
    @Test
    public void getSecondsTest() {
        System.out.println("now seconds : "+DateUtils.getSeconds(LocalDateTime.now()));
    }
}
