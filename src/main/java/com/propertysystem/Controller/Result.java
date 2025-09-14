package com.propertysystem.Controller;


import com.propertysystem.Constant.Code;
import io.swagger.v3.oas.annotations.media.Schema;


public class Result<T> {
    @Schema(name = "状态码")
    Integer code;
    @Schema(name = "状态信息")
    String msg;
    @Schema(name = "响应数据")
    T data = null;

    public Result() {
    }

    public Result(Code code) {
        this.code = code.getCode();
        this.msg = code.getMessage();
    }

    public Result(Code code, T data) {
        this.code = code.getCode();
        this.msg = code.getMessage();
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
