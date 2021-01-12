package com.kgc.kmall.interceptors;

import com.kgc.kmall.annotations.LoginRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断handler是否是后端handler
        if (handler.getClass().equals(org.springframework.web.method.HandlerMethod.class)){
           //使用反射获取方法上的LoginRequired注解
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if (methodAnnotation!=null){
                //判断methodAnnotation的value属性值
                boolean value=methodAnnotation.value();
                if (value){
                    //必须登录
                    System.out.println("拦截，必须登录");
                }
                System.out.println("拦截，但不需要登录登录");
            }
            System.out.println("没有添加注解不需要拦截");
        }
        System.out.println("测试拦截器");
        return true;
    }
}