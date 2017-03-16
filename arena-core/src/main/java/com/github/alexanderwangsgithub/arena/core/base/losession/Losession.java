package com.github.alexanderwangsgithub.arena.core.base.losession;

import com.github.alexanderwangsgithub.arena.core.common.bean.ABean;

public interface Losession {

    default void recycle() {}

    default Losession duplicate() {
        return ABean.deepCopy(this);
    }
}