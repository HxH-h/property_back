package com.propertysystem.Constant;

public enum Code {
    SUCCESS(200, "成功"),
    NEED_REFRESH(300, "需要刷新Token"),
    REQUEST_BLOCK(301 , "请求频繁"),
    LOGIN_FAIL(303, "登录失败"),
    NEED_LOGIN(304, "需要登录"),
    FORMAT_ERROR(305, "格式错误"),
    CODE_ERROR(306, "邮箱验证码错误"),
    CPACHA_ERROR(307, "图形验证码错误"),
    USER_EXIST(308 , "用户已存在"),
    EMAIL_EMPTY(309, "邮箱为空");




    private int code;
    private String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
