package com.yer.library.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BookType {
    FICTION("fiction"), NON_FICTION("non fiction"), OTHER("other");

    private static final Map<String, BookType> BOOK_TYPE_MAP = Stream
            .of(BookType.values())
            .collect(Collectors.toMap(s -> s.text, Function.identity()));

    private final String text;

    BookType(String text) {
        this.text = text;
    }

    @JsonCreator // This is the factory method and must be static
    public static BookType fromString(String string) {
        return Optional
                .ofNullable(BOOK_TYPE_MAP.get(string))
                .orElseThrow(() -> new IllegalArgumentException(string));
    }

    @Override
    public String toString() {
        return text;
    }
}
