package com.yer.library.service;

import java.util.Collection;

public interface CrudService<T> {
    T get(Long id);

    Collection<T> list(int limit);

    T add(T object);

    T fullUpdate(Long id, T object);

    Boolean delete(Long id);
    // public T partialUpdate(T object);
}
