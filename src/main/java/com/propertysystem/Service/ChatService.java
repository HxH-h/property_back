package com.propertysystem.Service;
import com.propertysystem.Constant.MessageStatus;
import com.propertysystem.Constant.MessageType;
import com.propertysystem.Constant.MsgConstant;
import com.propertysystem.Constant.RedisConstant;
import com.propertysystem.Controller.UserInfoThread;
import com.propertysystem.Controller.WebSocket;
import com.alibaba.fastjson.JSONObject;
import com.propertysystem.Controller.Pojo.Message;
import com.propertysystem.Dao.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ChatService {

    public static RedisTemplate redisTemplate;
    private UserMapper userMapper;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        ChatService.redisTemplate = redisTemplate;
    }

    public void SendMessage(String uuid , String event,Object message){
        JSONObject resp=new JSONObject();
        resp.put("event",event);
        resp.put(event,message);
        // 判断是否在线 在线则发送消息
        if (WebSocket.user.containsKey(uuid)){
            WebSocket.user.get(uuid).sendMessage(resp.toJSONString());
        }
    }
    public void syncMessage(Message msg){
        // 同步消息
        SendMessage(msg.receiver,"chat",msg);
        // 缓存消息
        messageCache(msg);
    }
    // 暂存到redis
    public void messageCache(Message msg){
        String front = msg.sender;
        String back = msg.receiver;
        int direction = 0; // send --> receive
        if (msg.sender.compareTo(msg.receiver) < 0){
            front = msg.receiver;
            back = msg.sender;
            direction = 1; // receive --> send
        }
        String key = RedisConstant.chat + front + ":" + back + ":" + msg.houseId;

        HashMap<String , Object> chatMessage = new HashMap<>();
        chatMessage.put("message" ,  msg.content);
        chatMessage.put("time" , msg.time);
        chatMessage.put("type" , msg.type.ordinal());
        chatMessage.put("status" , msg.status.ordinal());
        chatMessage.put("d" , direction);

        // 解析时间并获取时间戳用于排序
        LocalDateTime parsedTime = LocalDateTime.parse(msg.time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long timestamp = parsedTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();

        // 使用 JSON 序列化后存入 Redis Sorted Set
        redisTemplate.opsForZSet().add(key, chatMessage, timestamp);

        // 设置过期时间
        redisTemplate.expire(key, MsgConstant.CACHE_EXPIRE, TimeUnit.SECONDS);

    }
    // 获取聊天记录
    public List getMessage(String UserB , Long houseId){
        // 获取key
        // 获取当前用户的username
        String UserA = UserInfoThread.getInfo();

        // 构造key
        String front = UserA;
        String back = UserB;
        int direction = 0;
        if (UserA.compareTo(UserB) < 0){
            front = UserB;
            back = UserA;
            direction = 1;
        }
        String key = RedisConstant.chat + front + ":" + back + ":" + houseId;

        // 检查缓存
        List<Message> chatMessages = getMessageCache(front , back , houseId ,key , direction);

        int remainant = MsgConstant.MAX_MESSAGE_CNT - chatMessages.size();
        if (remainant > 0){
            // 获取数据库记录
            List<Message> msgs = userMapper.getMessage(UserA, UserB, houseId , MsgConstant.MAX_MESSAGE_CNT);
            chatMessages.addAll(msgs);
        }

        return chatMessages;
    }

    // 获取聊天记录缓存
    public List getMessageCache(String front , String back , Long houseId ,String key , int direction){

        // 检查缓存
        if (redisTemplate.hasKey(key)){
            Set<HashMap<String, Object>> rawMessages = redisTemplate.opsForZSet().reverseRange(key, 0, MsgConstant.MAX_MESSAGE_CNT - 1);
            List<Message> chatMessages = rawMessages.stream()
                    .map(data -> {
                        Message msg = Message.builder()
                                .content((String) data.get("message"))
                                .time((String) data.get("time"))
                                .houseId(houseId)
                                .sender((Integer)data.get("d") == 0 ? front : back)
                                .receiver((Integer)data.get("d") == 0 ? back : front)
                                .type(MessageType.values()[(Integer)data.get("type")])
                                .status(MessageStatus.values()[(Integer)data.get("status")])
                                .build();
                        return msg;
                    })
                    .collect(Collectors.toList());
            return chatMessages;
        }else return new ArrayList<>();

    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
