package com.ssafy.algonote.notification.service;

import com.ssafy.algonote.notification.dto.request.NotificationReqDto;
import com.ssafy.algonote.notification.repository.EmitterRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseEmitterService {

    private static final Long TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository sseEmitterRepository;

    public SseEmitter createEmitter(String emitterKey) {
        return sseEmitterRepository.save(emitterKey, new SseEmitter(TIMEOUT));
    }

    public void deleteEmitter(String emitterKey) {
        sseEmitterRepository.deleteById(emitterKey);
    }

    public void sendNotificationToClient(String emitterKey, NotificationReqDto notificationReqDto) {
        sseEmitterRepository.findById(emitterKey)
            .ifPresent(emitter -> send(notificationReqDto, emitterKey, emitter));
    }

    public void send(Object data, String emitterKey, SseEmitter sseEmitter) {
        try {
            log.info("send to client {}:[{}]", emitterKey, data);
            sseEmitter.send(SseEmitter.event()
                .id(emitterKey)
                .name("sse")
                .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException | IllegalStateException e) {
            log.error("IOException | IllegalStateException is occurred. ", e);
            sseEmitterRepository.deleteById(emitterKey);
        }
    }

}
