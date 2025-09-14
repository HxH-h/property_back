package com.propertysystem.Controller;

public class UserInfoThread {
    private static final ThreadLocal<String> info = new ThreadLocal<>();

    public static String getInfo(){
        return info.get();
    }
    public static void setInfo(String user){
        info.set(user);
    }
}
