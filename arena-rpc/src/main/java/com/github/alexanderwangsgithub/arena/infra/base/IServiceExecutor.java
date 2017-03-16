package com.github.alexanderwangsgithub.arena.infra.base;

import java.util.Map;

/**
 * arena
 *
 * @author Alexander Wang
 * @bio https://alexanderwangsgithub.github.io/
 * @email alexanderwangwork@outlook.com
 * @date 19/01/2017
 */
public interface IServiceExecutor {
    /**
     * @return executor info，include {name、group、executor、downgradedStrategy、provider}
     */
    default Object execInfo(){return null;}

    //static Map<String, Object>
}
