package com.github.alexanderwangsgithub.arena.core.base.possessor;

/**
 * arena
 *
 * @author Alexander Wang
 * @bio https://alexanderwangsgithub.github.io/
 * @email alexanderwangwork@outlook.com
 * @date 09/02/2017
 */

/**
 * Possessor is source owner
 * Possessor是资源的拥有者，它会在框架启动时实例化，它的实现类包括不限于redis、db、RMQ
 */
public interface Possessor {
    default void initialize(){}
    default void validate(){}
}
