package com.example.Qatu.service.impl;

import java.util.List;

import com.example.Qatu.repository.GenericRepo;
import com.example.Qatu.service.IGenericService;

public abstract class GenericService<T, ID> implements IGenericService<T, ID> {
    protected abstract GenericRepo<T, ID> getRepo();
    public static final String ID_NOT_FOUND_MESSAGE = "ID NOT FOUND: ";

    @Override
    public T save(T t) {
        return getRepo().save(t);
    }

    @Override
    public T update(T t, ID id) {

        if (!getRepo().existsById(id)) {
            throw new RuntimeException(ID_NOT_FOUND_MESSAGE + id);
        }

        return getRepo().save(t);
    }

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public T findById(ID id) {
        return getRepo().findById(id)
                .orElseThrow(() -> new RuntimeException(ID_NOT_FOUND_MESSAGE + id));
    }

    @Override
    public void delete(ID id) {

        if (!getRepo().existsById(id)) {
            throw new RuntimeException(ID_NOT_FOUND_MESSAGE + id);
        }

        getRepo().deleteById(id);
    }
}
