package com.ssafy.algonote.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();  // key는 EmitterId

    public SseEmitter save(String id, SseEmitter sseEmitter) {
        emitters.put(id, sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> findById(String id) {
        return Optional.ofNullable(emitters.get(id));
    }

    public void deleteById(String id) {
        emitters.remove(id);
    }

    public SseEmitter get(String id) {
        return emitters.get(id);
    }

    public Map<String, SseEmitter> findAllStartWithById(String id) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(id))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    public Map<String, Object> findAllEventCacheStartWithId(String memberId) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(memberId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}