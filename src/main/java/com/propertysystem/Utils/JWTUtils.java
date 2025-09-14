package com.propertysystem.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
//jwt工具类
public class JWTUtils {

    static String secretKey;
    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        JWTUtils.secretKey = secretKey;
    }

    static Long Expire;
    @Value("${jwt.expire}")
    public void setExpire(Long expire) {
        JWTUtils.Expire = expire;
    }

    static Long Refresh;
    @Value("${jwt.refresh}")
    public void setRefresh(Long refresh) {
        JWTUtils.Refresh = refresh;
    }


    public static String getJWT(String uuid){
        HashMap<String, String> info = new HashMap<>();
        info.put("uuid", uuid);
        return Jwts.builder()
                .setClaims(info)     //自定义载荷
                .signWith(SignatureAlgorithm.HS256, secretKey)    //签名算法
                .setExpiration(new Date(System.currentTimeMillis() + Expire))
                .compact();
    }
    public static String getRefreshJWT(String uuid){
        HashMap<String, String> info = new HashMap<>();
        info.put("uuid", uuid);
        return Jwts.builder()
                .setClaims(info)     //自定义载荷
                .signWith(SignatureAlgorithm.HS256, secretKey)    //签名算法
                .setExpiration(new Date(System.currentTimeMillis() + Refresh))
                .compact();
    }

    public static String parseJWT(String token ,String type){

        Claims body = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) body.get(type);

    }



}
