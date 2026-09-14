package com.valuego.notification.application;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitters {

    // Key 예시: "U_1" (로그인 유저 1번), "G_10" (게스트 멤버 10번)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String emitterKey, SseEmitter emitter) {
        emitters.put(emitterKey, emitter);

        emitter.onCompletion(() -> emitters.remove(emitterKey));
        emitter.onTimeout(() -> emitters.remove(emitterKey));
        emitter.onError((e) -> emitters.remove(emitterKey));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            emitters.remove(emitterKey);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendToClient(String emitterKey, Object data) {
        SseEmitter emitter = emitters.get(emitterKey);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
            } catch (IOException e) {
                emitters.remove(emitterKey);
                emitter.completeWithError(e);
            }
        }
    }
}
