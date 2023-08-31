package com.calcifer.weight.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.calcifer.weight.WeightApplication.TOKEN_USER_MAP;

public class AuthInterceptor implements HandlerInterceptor {
    @Value("${calcifer.weight.enable-auth:true}")
    private boolean enableAuth;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!enableAuth) return true;
        String token = request.getParameter("token");
        if (token != null && TOKEN_USER_MAP.containsKey(token)) {
            return true;
        }
        request.getRequestDispatcher("/adminx/login/notAuth").forward(request, response);
        return false;
    }

}
