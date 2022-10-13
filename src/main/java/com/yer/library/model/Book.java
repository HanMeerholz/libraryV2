package com.yer.library.model;

import com.yer.library.model.attributeconverters.YearAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Year;

@Entity(name = "Book")
@Table(
        name = "books",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "book_isbn_unique",
                        columnNames = "isbn"
                )
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @SequenceGenerator(
            name = "book_sequence",
            sequenceName = "book_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "isbn",
            nullable = false,
            columnDefinition = "VARCHAR(15)"
    )
    @NotEmpty(message = "ISBN cannot be empty or null")
    private String isbn;

    @Column(
            name = "title",
            nullable = false,
            columnDefinition = "TEXT"
    )
    @NotEmpty(message = "Title cannot be empty or null")
    private String title;

    @Column(
            name = "year",
            nullable = false,
            columnDefinition = "YEAR"
    )
    @Convert(
            converter = YearAttributeConverter.class
    )
    private Year year;

    @Column(
            name = "author",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String author;

    @Column(
            name = "type",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String type;


    @Column(
            name = "genre",
            columnDefinition = "TEXT"
    )
    private String genre;

    @Column(
            name = "value"
    )
    private Integer value;

    @Column(
            name = "deleted"
    )
    private Boolean deleted = false;

    public Book(String isbn, String title, Year year, String author, String type, String genre, Integer value) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.author = author;
        this.type = type;
        this.genre = genre;
        this.value = value;
    }
}
