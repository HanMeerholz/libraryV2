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

class BookTest {
    private static Validator validator;

    @BeforeEach
    public void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validBook() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    public void nullIsbn() {
        Book book = new Book(
                null,
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emptyIsbn() {
        Book book = new Book(
                "",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(2);
    }

    @Test
    public void invalidIsbn() {
        Book book = new Book(
                "978-123456789",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void invalidIsbn2() {
        Book book = new Book(
                "978-1234567890",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void invalidIsbn3() {
        Book book = new Book(
                "0-8422-2626-5",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nullTitle() {
        Book book = new Book(
                "978-2-3915-3957-4",
                null,
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void emptyTitle() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nullYear() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                null,
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    public void futureYear() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.now().plusYears(1),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nullAuthor() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                null,
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    public void nullType() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                null,
                BookGenre.HORROR,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nullGenre() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                null,
                4200
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void nullValue() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                null
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void negativeValue() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                -1000
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void valueTooHigh() {
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                500000
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void multipleViolations() {
        Book book = new Book(
                "978-2-3915-3957-1",
                "",
                Year.now().plusYears(10),
                "Cole Lyons",
                null,
                null,
                500000
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(6);
    }
}