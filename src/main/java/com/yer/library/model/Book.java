package com.yer.library.model;

import com.yer.library.model.attributeconverters.YearAttributeConverter;
import com.yer.library.model.enums.BookGenre;
import com.yer.library.model.enums.BookType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.ISBN;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Year;
import java.util.Objects;

@Entity(name = "Book")
@Table(
        name = "books" //,                               We shouldn't make ISBN unique because of soft delete.
//        uniqueConstraints = {                          ISBN of non-deleted books should be unique, should be
//                @UniqueConstraint(                     implemented with business logic.
//                        name = "book_isbn_unique",
//                        columnNames = "isbn"
//                )
//        }
)
@Getter
@Setter
@ToString
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
            columnDefinition = "VARCHAR(17)"
    )
    @NotBlank(message = "ISBN cannot be blank or null")
    @ISBN(message = "ISBN must be a valid ISBN 13")
    private String isbn;

    @Column(
            name = "title",
            nullable = false,
            columnDefinition = "TEXT"
    )
    @NotBlank(message = "Title cannot be blank or null")
    private String title;

    @Column(
            name = "year_published",
            nullable = false,
            columnDefinition = "SMALLINT"
    )
    @Convert(
            converter = YearAttributeConverter.class
    )
    @PastOrPresent(message = "Release year must be in the past")
    private Year year;

    @Column(
            name = "author",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false
    )
    @NotNull(message = "Must specify a book type (\"fiction\", \"non-fiction\", or \"other\")")
    private BookType type;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "genre"
    )
    @NotNull(message = "Must specify a book genre (\"other\" if no category fits)")
    private BookGenre genre;

    @Column(
            name = "book_value"
    )
    @NotNull
    @PositiveOrZero(message = "Book value cannot be negative")
    @Max(value = 99999, message = "Book value can be no more than 99999 ($999,99)")
    private Integer value;

    @Column(
            name = "deleted"
    )
    private Boolean deleted = false;

    public Book(String isbn, String title, Year year, String author, BookType type, BookGenre genre, Integer value) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.author = author;
        this.type = type;
        this.genre = genre;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Book book = (Book) o;
        return id != null && Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
