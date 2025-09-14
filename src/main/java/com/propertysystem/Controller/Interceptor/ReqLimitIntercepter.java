package com.propertysystem.Controller.Interceptor;

import com.propertysystem.Constant.Code;
import com.propertysystem.Constant.RedisConstant;
import com.propertysystem.Controller.Exception.CusException;
import com.propertysystem.CusAnno.RequestLimit;
import com.propertysystem.Utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ReqLimitIntercepter implements HandlerInterceptor {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取调用的方法对象，判断是否有限流的注解
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String name = handlerMethod.getMethod().getName();
            //获取方法上的限流注解
            RequestLimit reqLimit = handlerMethod.getMethodAnnotation(RequestLimit.class);

            //获取方法所属的类上的注解
            Class<?> declaringClass = handlerMethod.getMethod().getDeclaringClass();
            RequestLimit reqLimitClass = declaringClass.getAnnotation(RequestLimit.class);

            //获取request的ip地址
            String ipAddress = IpUtils.getIpAddress(request);

            if (reqLimit != null) {
                isLimit(ipAddress , name ,reqLimit);
            } else if (reqLimitClass != null) {
                isLimit(ipAddress , name ,reqLimitClass);
            }
        }

        return true;
    }
    public void isLimit(String ipAddress , String name ,RequestLimit reqLimit) throws CusException {
        String blockKey = RedisConstant.requestBlock + ipAddress + ":" + name;
        if (redisTemplate.hasKey(blockKey)) {
            // 被block
            log.info("ip: " + ipAddress + " 请求方法: " + name + " 被block了");
            throw new CusException(Code.REQUEST_BLOCK);
        } else {
            // 到redis查该ip的访问次数
            String cntKey = RedisConstant.requestCnt + ipAddress + ":" + name;
            Integer cnt = (Integer) redisTemplate.opsForValue().get(cntKey);
            if (cnt != null) {

                // 判断次数是否超过限制
                // 超过则拒绝访问，并加入block名单中，没超过则次数++
                if (cnt >= reqLimit.count()) {
                    // 被block
                    log.info("ip: " + ipAddress + " 请求方法: " + name + " 被block了");
                    redisTemplate.opsForValue().set(blockKey, 1, reqLimit.blockTime(), TimeUnit.MINUTES);
                    redisTemplate.delete(cntKey);
                    throw new CusException(Code.REQUEST_BLOCK);
                }else {
                    redisTemplate.opsForValue().increment(cntKey);
                }
            }else {
                redisTemplate.opsForValue().set(cntKey, 1, reqLimit.interval(), TimeUnit.MILLISECONDS);
            }
        }
    }
}
