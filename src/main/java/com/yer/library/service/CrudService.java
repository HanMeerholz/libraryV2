package com.yer.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.Collection;

public interface CrudService<T> {
    T get(Long id);

    Collection<T> list(int limit);

    T add(T object);

    T partialUpdate(Long id, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException;

    T fullUpdate(Long id, T object);

    Boolean delete(Long id);
}
