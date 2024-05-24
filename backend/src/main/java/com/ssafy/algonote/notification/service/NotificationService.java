package com.ssafy.algonote.notification.service;

import com.ssafy.algonote.exception.CustomException;
import com.ssafy.algonote.exception.ErrorCode;
import com.ssafy.algonote.member.domain.Member;
import com.ssafy.algonote.notification.domain.Notification;
import com.ssafy.algonote.notification.domain.NotificationType;
import com.ssafy.algonote.notification.dto.response.NotificationResDto;
import com.ssafy.algonote.notification.repository.EmitterRepository;
import com.ssafy.algonote.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional.TxType;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    /**
     * SseEmitter를 구독한다.
     *
     * @param memberId 알림을 수신할 회원의 memberId
     * @param lastEventId 마지막으로 수신한 알림의 eventId
     * @return 요청을 보낸 회원에 대한 SseEmitter 객체
     */
    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = makeTimeIncludedId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeTimeIncludedId(memberId);
        sendEvent(emitter, eventId, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        if (hasLostEvent(lastEventId)) {
            sendLostEvents(memberId, lastEventId, emitterId, emitter);
        }

        return emitter;
    }

    /**
     * memberId에 현재 시각을 붙여 id를 생성한다.
     *
     * @param memberId 회원 id
     * @return 현재 시각을 포함한 id
     */
    private static String makeTimeIncludedId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    /**
     * 클라이언트가 미수신한 event가 존재하는지에 대한 여부 반환한다.
     *
     * @param lastEventId 마지막으로 수신한 이벤트의 id
     * @return 미수신 event의 존재 여부
     */
    private static boolean hasLostEvent(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    /**
     * lastEventId를 기준으로 그 이후의 이벤트들을 전송한다.
     *
     * @param memberId 알림을 받을 회원의 memberId
     * @param lastEventId 마지막으로 받은 알림의 eventId
     * @param emitter 이벤트를 전송할 emitter
     */
    private void sendLostEvents(Long memberId, String lastEventId, String emitterId, SseEmitter emitter) {
        Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(memberId));
        events.entrySet().stream()
            .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)  // lastEventId < entry.getKey() 인 경우. 즉, entry.getKey() 가 lastEventId 보다 이후에 생성된 id인 경우
            .forEach(entry -> sendEvent(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    /**
     * SseEmitter로 event 전송
     *
     * @param emitterId 데이터를 받을 사용자의 아이디
     * @param data 전송할 데이터
     */
    private void sendEvent(SseEmitter emitter, String eventId, String emitterId, Object data) {
        if (emitter != null) {
            try {
                emitter.send(
                    SseEmitter.event()
                        .id(eventId)
                        .name("sse")
                        .data(data));
            } catch (IOException exception) {
                emitterRepository.deleteById(emitterId);
                emitter.completeWithError(exception);
            }
        }
    }

    /**
     * 외부 서비스 클래스에서 쓰일 알림 전송 메서드
     *
     * @param receiver 알림을 받을 회원
     * @param content 알림 내용
     */
    @Transactional(TxType.REQUIRES_NEW)
    public void notify(NotificationType notificationType, Member receiver, Member provider, Long relatedId, String relatedTag, String content) {
        log.info(relatedId + " 님에게 알림이 발송되었습니다.");
        Notification notification = notificationRepository.save(
            Notification.of(notificationType, receiver, provider, relatedId, relatedTag, content));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(String.valueOf(receiverId));
        sseEmitters.forEach(
            (key, emitter) -> {
                emitterRepository.saveEventCache(key, notification);
                sendEvent(emitter, eventId, key, NotificationResDto.from(notification));
            }
        );
    }

    /**
     * 특정 회원에게 전송된 알람 목록을 반환한다.
     *
     * @param memberId 회원 id
     * @return 알람 목록
     */
    public List<NotificationResDto> getList(Long memberId) {
        return notificationRepository.findAllByRecieverIdOrderByCreatedAtDesc(memberId)
            .stream()
            .map(NotificationResDto::from)
            .toList();
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));
//        notification.updateIsRead();
        notificationRepository.delete(notification);
    }

}