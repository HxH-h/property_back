package com.propertysystem.Controller;


import com.alibaba.fastjson.JSONObject;

import com.propertysystem.Constant.Code;
import com.propertysystem.Controller.Exception.CusException;
import com.propertysystem.Controller.Pojo.Message;
import com.propertysystem.Service.ChatService;
import com.propertysystem.Service.Task.DisConDetTask;
import com.propertysystem.Service.UserService;
import com.propertysystem.Utils.JWTUtils;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;



@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocket {

    private static final Logger log = LoggerFactory.getLogger(WebSocket.class);
    public static ConcurrentHashMap<String,WebSocket> user=new ConcurrentHashMap<>();

    public static ChatService chatService;
    @Autowired
    public void setChatService(ChatService chatService) {
        WebSocket.chatService = chatService;
    }

    Session session;
    String uuid;
    @OnOpen
    public void OnOpen(Session session, @PathParam("token") String token) throws IOException, CusException {
        if (token == null){
            log.info("token为空");
            session.close();
        }
        try {
            String uuid = JWTUtils.parseJWT(token, "uuid");
            this.uuid = uuid;
            this.session = session;
            // 判断是否重复登录
            if (user.containsKey(uuid)){
                // 踢出当前玩家
                user.get(uuid).sendMessage("{\"event\": \"exist\"}");
                user.get(uuid).session.close();
            }
            user.put(uuid,this);

            //更新websocket 连接时间
            DisConDetTask.playerLastTime.put(uuid,System.currentTimeMillis());

        }catch (Exception e){
            log.info(e.getMessage());
            session.close();
            throw new CusException(Code.NEED_LOGIN);
        }
    }

    @OnClose
    public void OnClose(){
        if (this.uuid != null && user.containsKey(this.uuid)){
            user.remove(this.uuid);
        }
    }
    @OnError
    public void OnError(Throwable e){
        e.printStackTrace();
        this.OnClose();
        log.info("意外关闭");
    }


    @OnMessage
    public void OnMessage(String message, Session session){
        Message msg = JSONObject.parseObject(message , Message.class);
        String event = msg.getEvent();
        msg.sender = this.uuid;
        if ("chat".equals(event)){
            chatService.syncMessage(msg);
        } else if ("ping".equals(event)) {
            this.heartCheck();
        }
        // 更新websocket连接时间
        DisConDetTask.playerLastTime.put(this.uuid,System.currentTimeMillis());
    }

    public void sendMessage(String message){
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void heartCheck(){
        sendMessage("pang");
    }
}
