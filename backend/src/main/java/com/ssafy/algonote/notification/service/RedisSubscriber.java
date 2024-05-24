package com.ssafy.algonote.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.algonote.notification.dto.request.NotificationReqDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseEmitterService sseEmitterService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel())
                    .substring("topics:".length());

            NotificationReqDto notificationDto = objectMapper.readValue(message.getBody(),
                NotificationReqDto.class);

            // 클라이언트에게 event 데이터 전송
            sseEmitterService.sendNotificationToClient(channel, notificationDto);
        } catch (IOException e) {
            log.error("IOException is occurred. ", e);
        }
    }
}