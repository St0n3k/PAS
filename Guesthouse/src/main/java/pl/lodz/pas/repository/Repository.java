package pl.lodz.pas.repository;

import java.util.List;

public interface Repository<T> {
    T add(T item);

    boolean remove(T item);

    T getById(Long id);

    T update(T item);

    List<T> getAll();
}
