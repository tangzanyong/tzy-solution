package com.tzy.solution.resubmit.annotation;

import java.lang.annotation.*;

/**
 * @author tangzanyong
 * @description 本地缓存锁
 * @date 2020/5/22
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LocalLock {

    String key() default "";

    /**
     * 过期时间 TODO 由于用的 guava 暂时就忽略这属性吧 集成 redis 需要用到
     */
    int expire() default 5;
}
