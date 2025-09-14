package com.propertysystem.Service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.propertysystem.Constant.CaptchaConstant;
import com.propertysystem.Constant.Code;
import com.propertysystem.Constant.RedisConstant;
import com.propertysystem.Controller.Exception.CusException;
import com.propertysystem.Controller.Pojo.LoginDTO;
import com.propertysystem.Controller.Pojo.Message;
import com.propertysystem.Controller.Pojo.RegisterDTO;
import com.propertysystem.Controller.Pojo.UserVO;
import com.propertysystem.Controller.UserInfoThread;
import com.propertysystem.Dao.Mapper.UserMapper;
import com.propertysystem.Dao.Pojo.User;
import com.propertysystem.Utils.EmailDO;
import com.propertysystem.Utils.EmailUtils;
import com.propertysystem.Utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.security.auth.login.LoginException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${indentity.code}")
    String identifycode;

    @Value("${indentity.expire}")
    Integer expire;

    public Map login(LoginDTO loginDTO) throws CusException {
        String pwd = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        String uuid = null;

        uuid = userMapper.selectByEmail(loginDTO.getUsername(), pwd);

        if (uuid != null){
            // 生成token
            String accessToken = JWTUtils.getJWT(uuid);

            // 生成refreshToken
            String refreshToken = JWTUtils.getRefreshJWT(uuid);
            // 返回token
            Map<String , String> token = new HashMap<>();
            token.put("accessToken", accessToken);
            token.put("refreshToken", refreshToken);
            return token;
        }else {
            throw new CusException(Code.LOGIN_FAIL);
        }
    }

    public String getAccessToken(String token) {
        String uuid = JWTUtils.parseJWT(token, "uuid");
        return JWTUtils.getJWT(uuid);
    }

    public void register(RegisterDTO registerDTO) throws CusException {
        //校对验证码
        String code = (String)redisTemplate.opsForValue().get(RedisConstant.IndentityCode + registerDTO.getEmail());

        if (code != null && code.equals(registerDTO.getCode())){

            redisTemplate.delete(RedisConstant.IndentityCode + registerDTO.getEmail());

            //校对邮箱格式和用户名格式

            if (registerDTO.getEmail().matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")&&
                    registerDTO.getUsername().matches("^[A-Za-z][A-Za-z0-9!#$%^&*]*$")){
                //验证用户名和邮箱是否重复
                String []uuid = userMapper.playerExsit(registerDTO.getUsername(), registerDTO.getEmail());
                if (uuid == null || uuid.length == 0){
                    //密码加密
                    String pwd = DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes());
                    //插入数据库
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    String format = formatter.format(date);

                    User user = User.builder()
                            .uuid(UUID.randomUUID().toString().replaceAll("-",""))
                            .username(registerDTO.getUsername())
                            .password(pwd)
                            .email(registerDTO.getEmail())
                            .role(registerDTO.isRole())
                            .create(format)
                            .build();
                    userMapper.addUser(user);

                }else {
                    throw new CusException(Code.USER_EXIST);
                }
            }else {
                throw new CusException(Code.FORMAT_ERROR);
            }
        }else {
            throw new CusException(Code.CODE_ERROR);
        }

    }

    public String getCaptcha(String key) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(CaptchaConstant.CAPTCHA_WIDTH,
                CaptchaConstant.CAPTCHA_HEIGHT,
                CaptchaConstant.CAPTCHA_LENGTH,
                CaptchaConstant.CAPTCHA_NUMBER);
        String code = lineCaptcha.getCode();
        redisTemplate.opsForValue().set(RedisConstant.CaptchaCode+key, code, expire, TimeUnit.MINUTES);
        return "data:image/png;base64," + lineCaptcha.getImageBase64();
    }

    public boolean verifyCaptcha(String key, String code) {
        String cap = (String) redisTemplate.opsForValue().get(RedisConstant.CaptchaCode + key);
        if (cap != null && cap.equals(code)){
            redisTemplate.delete(RedisConstant.CaptchaCode + key);
            return true;
        }
        return false;
    }

    public void getCode(String email) throws CusException {

        if (email == null){
            throw new CusException(Code.EMAIL_EMPTY);
        }
        String code = "";
        Random rand = new Random();
        for (int i = 0; i < 6; i++){
            code += identifycode.charAt(rand.nextInt(identifycode.length()));
        }

        redisTemplate.opsForValue().set(RedisConstant.IndentityCode + email, code,expire, TimeUnit.MINUTES);
        EmailUtils.sendEmail(new EmailDO(new String[]{email}, "验证码", "您的验证码是：" + code + "，请在" + expire + "分钟内完成验证。"));

    }

    public UserVO getPlayerInfo() {
        String uuid = UserInfoThread.getInfo();
        return userMapper.getUserInfo(uuid);
    }


}
