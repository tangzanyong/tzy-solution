package com.tzy.solution.resubmit.config2;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author tangzanyong
 * @description key生成器(Key 生成策略（接口）: 具体实现由使用者自己去注入)
 * @date 2020/5/26
 **/
public interface CacheKeyGenerator {
    /**
     * 获取AOP参数,生成指定缓存Key
     * @param pjp PJP
     * @return 缓存KEY
     */
    String getLockKey(ProceedingJoinPoint pjp);
}
