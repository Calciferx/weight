package com.calcifer.weight.interceptor;

import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.dto.UserDTO;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

public class AuthInterceptor implements HandlerInterceptor {
    @Value("${calcifer.weight.enable-auth:true}")
    private boolean enableAuth;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!enableAuth) return true;
        UserDTO userDTO = (UserDTO)request.getSession().getAttribute("user");
        if (userDTO != null){
            WeightContext.CURRENT_SESSION.get().setUserDTO(userDTO);
            return true;
        }

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api")) {
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                RespWrapper<Void> resp = new RespWrapper<>(false, RespCodeEnum.NOT_LOGIN_ERROR);
                writer.write(objectMapper.writeValueAsString(resp));
            }
        } else {
            response.sendRedirect("/login.html");
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        WeightContext.CURRENT_SESSION.remove();
    }
}
