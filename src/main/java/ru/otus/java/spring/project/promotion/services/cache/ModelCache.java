package ru.otus.java.spring.project.promotion.services.cache;

import java.util.Collection;
import java.util.List;

public interface ModelCache<T> {

    T get(long id);

    List<T> getByIds(Collection<Long> ids);

    void put(T t);

    void remove(long id);

    int size();

    boolean isEmpty();

    void putAll(Collection<T> models);

    List<T> getAll();

}
