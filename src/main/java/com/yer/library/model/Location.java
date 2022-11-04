package com.yer.library.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
    public static final Short NR_OF_FLOORS = 3;
    public static final Short MAX_BOOKCASES = 100;
    public static final Short MAX_SHELVES = 15;

    private Short floor;
    private Short bookcase;
    private Short shelve;
}
