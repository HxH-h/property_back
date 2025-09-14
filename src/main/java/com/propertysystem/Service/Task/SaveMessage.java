package com.propertysystem.Service.Task;


import com.propertysystem.Constant.MessageStatus;
import com.propertysystem.Constant.MessageType;
import com.propertysystem.Constant.RedisConstant;
import com.propertysystem.Controller.Pojo.Message;
import com.propertysystem.Dao.Mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

// 定时持久化消息
@Slf4j
@Service
public class SaveMessage {

    @Autowired
    UserMapper userMapper;

    // 操作redis
    public static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        SaveMessage.redisTemplate = redisTemplate;
    }

    public Set<String> getAllChatKeys() {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(RedisConstant.chat + "*").build();

        Cursor<byte[]> cursor = (Cursor<byte[]>) redisTemplate.execute((RedisCallback<Cursor<byte[]>>) connection -> {
            return connection.scan(options);
        });

        while (cursor != null && cursor.hasNext()) {
            byte[] keyBytes = cursor.next();
            String key = new String(keyBytes);
            keys.add(key);
        }

        return keys;
    }

    public Message convertToChatMessage(HashMap<String, Object> data, String front, String back , Long houseId) {
        String message = (String) data.get("message");
        String time = (String) data.get("time");
        int status = (Integer) data.get("status");
        int type = (Integer) data.get("type");


        Message chatMessage = Message.builder()
                .sender((Integer)data.get("d") == 0 ? front : back)
                .receiver((Integer)data.get("d") == 0 ? back : front)
                .houseId(houseId)
                .content(message)
                .time(time)
                .status(MessageStatus.values()[status])
                .type(MessageType.values()[type])
                .build();
        return chatMessage;
    }

    //@Scheduled(fixedRate = 3600000)
    @Scheduled(fixedDelay = 150000)
    public void saveMessage() {
        // 封装为ChatMessage
        List<Message> result = new ArrayList<>();
        Set<String> keys = getAllChatKeys();

        for (String key : keys) {

            String[] split = key.split(":");
            String front = split[2];
            String back = split[3];
            Long houseId = Long.parseLong(split[4]);

            Set<HashMap<String, Object>> messages = redisTemplate.opsForZSet().range(key, 0, -1);
            if (messages != null){
                for (HashMap<String, Object> msg : messages) {
                    Message chatMessage = convertToChatMessage(msg, front, back, houseId);
                    result.add(chatMessage);
                }
            }
            redisTemplate.delete(key);
        }

        // 存储到数据库
        if (!result.isEmpty()) userMapper.saveMessage(result);
        log.info("保存了{}条消息", result.size());
    }
}
