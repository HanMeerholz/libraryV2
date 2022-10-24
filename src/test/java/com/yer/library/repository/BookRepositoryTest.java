package com.yer.library.repository;

import com.yer.library.model.Book;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.ofSize;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByIsbnThatExists() {
        // given
        String isbn = "978-2-3915-3957-4";
        Book book = new Book(
                isbn,
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );

        underTest.save(book);

        // when
        Optional<Book> actual = underTest.findByIsbn(isbn);

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isNotNull();
        assertThat(actual.get().getIsbn()).isEqualTo(isbn);
    }

    @Test
    void findByIsbnThatDoesNotExist() {
        // given
        String isbn = "978-2-3915-3957-4";

        // when
        Optional<Book> actual = underTest.findByIsbn(isbn);

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void listAvailableEmptyDatabase() {
        // when
        List<Book> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listAvailableOnlyDeletedBooks() {
        // given
        Book book1 = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        book1.setDeleted(true);
        Book book2 = new Book(
                "978-0-1011-1658-9",
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );
        book2.setDeleted(true);

        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2));
        underTest.saveAll(books);

        // when
        List<Book> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listAvailableOneAvailableBook() {
        // given
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        underTest.save(book);

        // when
        List<Book> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(1).contains(book);
    }

    @Test
    void listAvailableMultipleAvailableBooks() {
        // given
        Book book1 = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        Book book2 = new Book(
                "978-0-1011-1658-9",
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );

        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2));
        underTest.saveAll(books);

        // when
        List<Book> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(2).contains(book1).contains(book2);
    }

    @Test
    void listAvailableMultipleAvailableAndDeletedBooks() {
        // given
        Book book1 = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        book1.setDeleted(true);

        Book book2 = new Book(
                "978-0-1011-1658-9",
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );

        Book book3 = new Book(
                "978-0-6967-9461-2",
                "Case of the Laughing Baboon",
                Year.of(1945),
                "Murat McCartney",
                "fiction",
                "fairy tale",
                4200
        );
        book3.setDeleted(true);

        Book book4 = new Book(
                "978-6-3073-8763-1",
                "The Serpent in the Stars",
                Year.of(1995),
                "Tyra Daniels",
                "nonfiction",
                "psychology",
                4200
        );

        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2, book3, book4));
        underTest.saveAll(books);

        // when
        List<Book> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .doesNotContain(book1)
                .contains(book2)
                .doesNotContain(book3)
                .contains(book4);
    }

    @Test
    void listAvailablePageSizeSmallerThanTotalAmountOfBooks() {
        // given
        Book book1 = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        Book book2 = new Book(
                "978-0-1011-1658-9",
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );
        Book book3 = new Book(
                "978-0-6967-9461-2",
                "Case of the Laughing Baboon",
                Year.of(1945),
                "Murat McCartney",
                "fiction",
                "fairy tale",
                4200
        );
        Book book4 = new Book(
                "978-6-3073-8763-1",
                "The Serpent in the Stars",
                Year.of(1995),
                "Tyra Daniels",
                "nonfiction",
                "psychology",
                4200
        );

        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2, book3, book4));
        underTest.saveAll(books);

        // when
        List<Book> actual = underTest.listAvailable(ofSize(3));

        // then
        // at least 3 books of the list "books" appear in actual
        assertThat(actual).hasSize(3).areAtLeast(3, new Condition<>(books::contains, "containsNBooksOf"));
    }
}