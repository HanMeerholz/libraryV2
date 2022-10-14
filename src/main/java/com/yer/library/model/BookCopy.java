package com.yer.library.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "BookCopy")
@Table(name = "book_copies")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookCopy {
    @Id
    @SequenceGenerator(
            name = "book_copy_sequence",
            sequenceName = "book_copy_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_copy_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "book_id",
            foreignKey = @ForeignKey(name = "FK_book_copies_books")
    )
    private Book book;

    @Columns(columns = {
            @Column(name = "loc_floor"),
            @Column(name = "loc_bookcase"),
            @Column(name = "loc_shelve")
    })
    @Type(type = "com.yer.library.model.customtypes.LocationType")
    private Location location;

    @Column(name = "deleted")
    private Boolean deleted = false;

    public BookCopy(Book book, Location location) {
        this.book = book;
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BookCopy bookCopy = (BookCopy) o;
        return id != null && Objects.equals(id, bookCopy.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
