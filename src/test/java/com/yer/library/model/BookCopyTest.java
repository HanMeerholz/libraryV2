package com.yer.library.model;

import com.yer.library.model.enums.BookGenre;
import com.yer.library.model.enums.BookType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookCopyTest {
    private static Validator validator;

    @BeforeEach
    public void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validBookCopy() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        Set<ConstraintViolation<BookCopy>> violations = validator.validate(bookCopy);
        assertThat(violations).isEmpty();
    }

    @Test
    public void nullLocation() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        BookCopy bookCopy = new BookCopy(
                book,
                null
        );
        Set<ConstraintViolation<BookCopy>> violations = validator.validate(bookCopy);
        assertThat(violations).isEmpty();
    }

    @Test
    public void locationFieldsNull() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        BookCopy bookCopy = new BookCopy(
                book,
                new Location(null, null, null)
        );
        Set<ConstraintViolation<BookCopy>> violations = validator.validate(bookCopy);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void locationFieldNull() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, null, (short) 1)
        );
        Set<ConstraintViolation<BookCopy>> violations = validator.validate(bookCopy);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void locationFieldInvalidValue() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) -1, (short) 1, (short) 1)
        );
        Set<ConstraintViolation<BookCopy>> violations = validator.validate(bookCopy);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void locationFieldInvalidValue2() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) (Location.MAX_SHELVES + 1))
        );
        Set<ConstraintViolation<BookCopy>> violations = validator.validate(bookCopy);
        assertThat(violations).hasSize(1);
    }

}