package com.yer.library.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BookGenre {
    CLASSIC("classic"),
    CONTEMPORARY("contemporary"),
    REALIST("realist"),
    HISTORICAL("historical"),
    DRAMA("drama"),
    MYSTERY("mystery"),
    CRIME("crime"),
    ACTION_AND_ADVENTURE("action & adventure"),
    THRILLER("thriller"),
    ROMANCE("romance"),
    COMING_OF_AGE("coming-of-age"),
    HORROR("horror"),
    FANTASY("fantasy"),
    SCIENCE_FICTION("science fiction"),
    WESTERN("western"),
    FOLKLORE("folklore"),
    CHILDRENS("children's"),
    YOUNG_ADULT("young adult"),
    COMIC_BOOK("comic book"),
    GRAPHIC_NOVEL("graphic novel"),
    POETRY("poetry"),
    COMEDY("comedy"),
    SATIRE("satire"),
    PHILOSOPHICAL("philosophical"),
    RELIGIOUS("religious");

    private static final Map<String, BookGenre> BOOK_GENRE_MAP = Stream
            .of(BookGenre.values())
            .collect(Collectors.toMap(s -> s.text, Function.identity()));

    private final String text;

    BookGenre(String text) {
        this.text = text;
    }

    @JsonCreator // This is the factory method and must be static
    public static BookGenre fromString(String string) {
        return Optional
                .ofNullable(BOOK_GENRE_MAP.get(string))
                .orElseThrow(() -> new IllegalArgumentException(string));
    }

    @Override
    public String toString() {
        return text;
    }
}