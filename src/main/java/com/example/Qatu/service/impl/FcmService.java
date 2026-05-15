package com.example.Qatu.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {

    // Envía notificación push al dispositivo del vendedor
    public void enviarNotificacion(
            String fcmToken,
            String titulo,
            String cuerpo,
            String tipo) {

        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM token vacío — no se envió notificación");
            return;
        }

        try {
            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle(titulo)
                    .setBody(cuerpo)
                    .build())
                .putData("tipo", tipo)
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Notificación FCM enviada — messageId={}", response);

        } catch (FirebaseMessagingException e) {
            log.error("Error enviando FCM: {}", e.getMessage());
        }
    }
}