package com.propertysystem.Service.Task;

import com.propertysystem.Controller.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

// 断线检测任务
@Slf4j
@Component
public class DisConDetTask {
    public static ConcurrentHashMap <String, Long> playerLastTime = new ConcurrentHashMap<>();

    // 超过一分钟则断开连接
    private final Long interval = 60000L;

    // 每隔一分钟检测一次
    // 定时检测断线
    @Scheduled(fixedDelay = 60000)
    public void check(){
        long now = System.currentTimeMillis();
        // 超时则断开连接
        // 没有顺序要求，可以并行处理
        playerLastTime.entrySet().parallelStream()
                .filter(entry -> entry.getValue() + interval < now)
                .forEach(entry -> {
                    // 检测是否为正常退出
                    String key = entry.getKey();
                    if (WebSocket.user.containsKey(key)){
                        WebSocket.user.get(key).OnClose();
                        log.info("用户 " + key + " 掉线");
                    }
                });

    }
}
