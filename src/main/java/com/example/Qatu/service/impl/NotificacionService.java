package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Notificacion;
import com.example.Qatu.repository.NotificacionRepo;
import com.example.Qatu.service.INotificacionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificacionService extends GenericService<Notificacion, Integer> implements INotificacionService {
    private final NotificacionRepo repo;
    @Override
    protected NotificacionRepo getRepo() {
        return repo;
    }
}
