package pl.lodz.p.it.pas.repository;

import java.util.List;
import java.util.Optional;

public interface CustomRepository<T> {
    T add(T item);

    boolean remove(T item);

    Optional<T> getById(Long id) throws Exception;

    Optional<T> update(T item) throws Exception;

    List<T> getAll();
}
