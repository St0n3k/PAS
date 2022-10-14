package pl.lodz.nbd.repository;

import jakarta.persistence.EntityManager;

import java.util.List;

public interface Repository<T> {
    T add(T item);

    boolean remove(T item);

    T getById(Long id);

    T update(T item);

    List<T> getAll();
}
