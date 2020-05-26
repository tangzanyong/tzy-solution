package com.tzy.solution.resubmit.config;

import com.tzy.solution.resubmit.annotation.Token;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author tangzanyong
 * @description 使用拦截器拦截请求，在拦截器里面用java的UUID生成一个随机的UUID并把这个UUID放到session里面，
 * 然后在浏览器做数据提交的时候将此UUID提交到服务器。服务器在接收到此UUID后，检查一下该UUID是否已经被提交，如果已经被提交，则不让逻辑继续执行下去…**
 *      1.拦截@Token(save=true)注解的请求，生成随机的UUID做为token放到session里面，并把token返回给前端页面
 *      2.前端页面提交时，拦截@Token(remove=true)请求，服务器收到token与session里的token比较，如不相同提交重复提交终止请求，如相同继续执行下去...
 *      3.清除session里token的值
 * @date 2020/5/22
 **/
public class TokenInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Token annotation = method.getAnnotation(Token.class);
//            System.out.println("session null:" + request.getSession());
//            System.out.println("session id:" + request.getSession().getId());
            if (annotation != null) {
                boolean needSaveSession = annotation.save();
                if (needSaveSession) {
                    String token = UUID.randomUUID().toString();
                    System.out.println("token:" + token);
                    //request.getSession(false).setAttribute("token", token);
                    request.getSession().setAttribute("token", token);
                }
                boolean needRemoveSession = annotation.remove();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                        //throw new RuntimeException("请勿重复请求");
                        System.out.println("请勿重复请求");
                        return false;
                    }
                    request.getSession(false).removeAttribute("token");
                }
            }
            //System.out.println("正常访问");
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = (String) request.getSession(false).getAttribute("token");
        if (serverToken == null) {
            return true;
        }
        String clinetToken = request.getParameter("token");
        if (clinetToken == null) {
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }
}
