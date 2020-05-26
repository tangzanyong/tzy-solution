package com.tzy.solution.resubmit.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tzy.solution.resubmit.annotation.LocalLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author tangzanyong
 * @description 1.拦截@LocalLock注解的请求生成一个key,这个key生成有多种方式(如:key=方法名:参数1...参数n; key=sessionId+请求路径等)只要唯一即可
 *              2.判断key在缓存中是否存，如果存在提示重复提交 终止请求，否则就正常执行...
 *              3.缓存key，如果单体应用缓存本地即可，如果是分布式应用 缓存到所以应用共用的缓存
 *              4.清除key(注意key的失效时间设置)
 * @date 2020/5/22
 **/
@Aspect
@Configuration
public class LockMethodInterceptor {
    //这里定义一个google的Gruava缓存，只适合本地单机版，如果要支持集群、分布式服务可以换成共用的redis缓存
    private static final Cache<String, Object> CACHES = CacheBuilder.newBuilder()
            // 最大缓存 100 个
            .maximumSize(1000)
            // 设置写缓存后 5 秒钟过期
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    @Around("execution(public * *(..)) && @annotation(com.tzy.solution.resubmit.annotation.LocalLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        LocalLock localLock = method.getAnnotation(LocalLock.class);
        String key = getKey(localLock.key(), pjp.getArgs());
        if (!StringUtils.isEmpty(key)) {
            //存在一样的key表示重复提交
            if (CACHES.getIfPresent(key) != null) {
                throw new RuntimeException("请勿重复请求");
            }
            // 如果是第一次请求,就将 key 当前对象压入缓存中
            CACHES.put(key, key);

            System.out.println(CACHES.asMap());
            System.out.println(CACHES.getIfPresent(key));
        }
        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException("服务器异常");
        } finally {
            // TODO 为了演示效果,这里就不调用 CACHES.invalidate(key);
            //CACHES.invalidate(key); //使其key失效(清除key)
        }
    }

    /**
     * key 的生成策略,如果想灵活可以写成接口与实现类的方式
     * key 的生成策略:使用@LocalLock注解自定义 如：@LocalLock(key = "book:arg[0]...arg[n]"),arg[0]会替换成方法参数值
     * @param keyExpress 表达式
     * @param args    参数
     * @return 生成的key
     */
    private String getKey(String keyExpress, Object[] args) {
        for (int i = 0; i < args.length; i++) {
            keyExpress = keyExpress.replace("arg[" + i + "]", args[i].toString());
        }
        return keyExpress;
    }

    /**
     * key 的生成策略: sessionId + 请求路径
     * @return 生成的key
     */
    private String getKey() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
        HttpServletRequest request = attributes.getRequest();
        String key = sessionId + "-" + request.getServletPath();
        return key;
    }
}
