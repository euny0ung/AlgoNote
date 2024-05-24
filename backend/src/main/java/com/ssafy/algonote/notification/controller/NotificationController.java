package com.ssafy.algonote.notification.controller;

import com.ssafy.algonote.config.security.SecurityUtil;
import com.ssafy.algonote.notification.dto.response.NotificationResDto;
import com.ssafy.algonote.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Notification API", description = "알림 관련 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
        summary = "서버 구독 요청",
        description = "SseEmitter를 반환합니다."
    )
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Long memberId = SecurityUtil.getMemberId();
        log.info("subscribe memberId: {}, lastEventId: {}", memberId, lastEventId);
        return notificationService.subscribe(memberId, lastEventId);
    }

    @Operation(
        summary = "알림 목록 조회",
        description = "알림 목록을 최신순으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<NotificationResDto>> getList() {
        Long memberId = SecurityUtil.getMemberId();
        return ResponseEntity.ok(notificationService.getList(memberId));
    }

    @Operation(
        summary = "알림 읽음 처리",
        description = "알림을 읽음 처리합니다."
    )
    @DeleteMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

}