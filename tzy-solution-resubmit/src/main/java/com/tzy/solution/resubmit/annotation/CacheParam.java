package com.tzy.solution.resubmit.annotation;

import java.lang.annotation.*;

/**
 * @author tangzanyong
 * @description 锁的参数
 * @date 2020/5/26
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {
    /**
     * 字段名称
     * @return String
     */
    String name() default "";
}
