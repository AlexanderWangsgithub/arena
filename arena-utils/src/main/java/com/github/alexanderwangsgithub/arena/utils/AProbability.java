package com.github.alexanderwangsgithub.arena.utils;

import java.util.Random;

/**
 * arena
 *
 * @author Alexander Wang
 * @bio https://alexanderwangsgithub.github.io/
 * @email alexanderwangwork@outlook.com
 * @date 19/01/2017
 */
public final class AProbability {
    /**
     * 10%概率返回true
     * @return
     */
    public static boolean tenPercent() {
        Random random = new Random();
        return random.nextInt(9) == 1;
    }

    /**
     * 1/n概率返回true
     * @return
     */
    public static boolean percent(int n) {
        Random random = new Random();
        return random.nextInt(n) == 1;
    }
}
