package com.github.alexanderwangsgithub.arena.utils;

import java.util.Random;

/**
 * 这是一个String的工具类
 * Created by wanggang on 03/01/2017.
 */
public final class AString {
    private static StringBuilder wordsNumber = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int range = wordsNumber.length();
        for (int i = 0; i < length; i++) {
            sb.append(wordsNumber.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    public static boolean isNumeric(String string) {
        return string != null && string.matches("-?\\d+(\\.\\d+)?");
    }

}
