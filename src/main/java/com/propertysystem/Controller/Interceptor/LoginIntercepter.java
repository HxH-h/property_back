package com.propertysystem.Controller.Interceptor;

import com.propertysystem.Constant.Code;
import com.propertysystem.Controller.Exception.CusException;
import com.propertysystem.Controller.UserInfoThread;
import com.propertysystem.Utils.IpUtils;
import com.propertysystem.Utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginIntercepter implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginIntercepter.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        try {
            String uuid = JWTUtils.parseJWT(token, "uuid");
            UserInfoThread.setInfo(uuid);

        } catch (Exception e) {
            // 获取IP
            String ipAddress = IpUtils.getIpAddress(request);
            log.info("拦截ip: " + ipAddress + " 拦截请求 " + request.getRequestURI());
            throw new CusException(Code.NEED_REFRESH);
        }

        return true;
    }
}
