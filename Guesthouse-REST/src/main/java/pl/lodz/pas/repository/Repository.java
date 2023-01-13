package pl.lodz.pas.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    T add(T item);

    void remove(T item);

    Optional<T> getById(Long id);

    Optional<T> update(T item);

    List<T> getAll();
}
