package com.propertysystem.Controller;

import com.propertysystem.Constant.Code;
import com.propertysystem.Controller.Exception.CusException;
import com.propertysystem.Controller.Pojo.LoginDTO;
import com.propertysystem.Controller.Pojo.RegisterDTO;
import com.propertysystem.Controller.Pojo.UserVO;
import com.propertysystem.CusAnno.RequestLimit;
import com.propertysystem.Service.ChatService;
import com.propertysystem.Service.UserService;
import com.propertysystem.Utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "登录注册")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    ChatService chatService;

    @PostMapping("/api/login")
    @Operation(summary = "登录接口")
    @RequestLimit(count = 5)
    public Result<HashMap> login(@RequestBody LoginDTO loginDTO) throws CusException {
        Map jwt = userService.login(loginDTO);
        return new Result(Code.SUCCESS, jwt);
    }

    @GetMapping("/api/getAccessToken")
    @Operation(summary = "获取短期Token")
    public Result getAccessToken(HttpServletRequest request) throws CusException {
        String jwt = request.getHeader("Authorization");
        try {
            String accessToken = userService.getAccessToken(jwt);
            return new Result<>(Code.SUCCESS , accessToken);
        }catch (Exception e){
            throw new CusException(Code.NEED_LOGIN);
        }
    }


    @PostMapping("/api/register/register")
    @Operation(summary = "注册接口")
    public Result register(@RequestBody RegisterDTO registerDTO) throws CusException {
        userService.register(registerDTO);
        return new Result(Code.SUCCESS);
    }


    @GetMapping("/api/register/genCaptcha")
    @Operation(summary = "获取图形验证码")
    public String genCaptcha(HttpServletRequest request, HttpServletResponse response){
        //获取ip地址和浏览器信息 作为key
        String ip = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String key = ip + userAgent;

        String img = userService.getCaptcha(key);
        return img;
    }


    @GetMapping("/api/register/genCode/{email}/{captcha}")
    @Operation(summary = "生成验证码")
    public Result genCode(@PathVariable String email, @PathVariable String captcha) throws CusException {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String ip = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String key = ip + userAgent;
        if (userService.verifyCaptcha(key, captcha)){
            userService.getCode(email);
            return new Result<>(Code.SUCCESS);
        }else {
            return new Result<>(Code.CPACHA_ERROR);
        }
    }

    @GetMapping("/api/getUserInfo")
    @Operation(summary = "获取用户基本信息")
    public Result<UserVO> getPlayerInfo(){
        UserVO playerInfo = userService.getPlayerInfo();
        return new Result<>(Code.SUCCESS, playerInfo);
    }

}
