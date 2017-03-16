package com.github.alexanderwangsgithub.arena.core.base.possessor;

/**
 * Possessor is source owner, it is not threadLocal.
 * Possessor是资源的拥有者，工具属性，它会在框架启动时实例化，它的实现类包括不限于redis、db、RMQ
 * Possessor不是ThreadLocal的，是全局的工具类，会在Arena启动的时候初始化。
 */
