package com.example.Qatu.service.impl;

import com.google.firebase.FirebaseApp;
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

    public void enviarNotificacionConRuta(
            String fcmToken, String titulo, String cuerpo,
            double latDestino, double lngDestino) {

        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM token vacío — ruta sugerida no enviada");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("FCM no disponible — ruta sugerida no enviada");
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .putData("tipo", "RUTA_SUGERIDA")
                    .putData("latDestino", String.valueOf(latDestino))
                    .putData("lngDestino", String.valueOf(lngDestino))
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Ruta sugerida enviada — messageId={}", response);

        } catch (FirebaseMessagingException e) {
            log.error("Error enviando ruta sugerida FCM: {}", e.getMessage());
        }
    }
}