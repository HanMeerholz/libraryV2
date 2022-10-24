package com.yer.library.repository;

import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.model.Location;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.ofSize;

@DataJpaTest
class BookCopyRepositoryTest {

    @Autowired
    private BookCopyRepository underTest;
    @Autowired
    private BookRepository bookRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void listAvailableEmptyDatabase() {
        // when
        List<BookCopy> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listAvailableOnlyDeletedBookCopies() {
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

        BookCopy bookCopy1 = new BookCopy(
                book1,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy1.setDeleted(true);

        BookCopy bookCopy2 = new BookCopy(
                book1,
                new Location((short) 2, (short) 1, (short) 1)
        );
        bookCopy2.setDeleted(true);

        BookCopy bookCopy3 = new BookCopy(
                book2,
                new Location((short) 1, (short) 1, (short) 2)
        );
        bookCopy3.setDeleted(true);

        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2));
        bookRepository.saveAll(books);
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2, bookCopy3));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listAvailableMultipleAvailableBookCopies() {
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
        BookCopy bookCopy1 = new BookCopy(
                book1,
                new Location((short) 1, (short) 1, (short) 1)
        );
        BookCopy bookCopy2 = new BookCopy(
                book1,
                new Location((short) 2, (short) 1, (short) 1)
        );
        BookCopy bookCopy3 = new BookCopy(
                book2,
                new Location((short) 1, (short) 1, (short) 2)
        );

        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2));
        bookRepository.saveAll(books);
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2, bookCopy3));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(3).contains(bookCopy1).contains(bookCopy2).contains(bookCopy3);
    }

    @Test
    void listAvailableMultipleAvailableAndDeletedBooksAndBookCopies() {
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
        book3.setDeleted(true);

        // given
        BookCopy bookCopy1 = new BookCopy(
                book1,
                new Location((short) 1, (short) 1, (short) 1)
        );
        BookCopy bookCopy2 = new BookCopy(
                book1,
                new Location((short) 2, (short) 1, (short) 1)
        );
        bookCopy2.setDeleted(true);
        BookCopy bookCopy3 = new BookCopy(
                book1,
                new Location((short) 2, (short) 1, (short) 2)
        );

        BookCopy bookCopy4 = new BookCopy(
                book2,
                new Location((short) 1, (short) 1, (short) 2)
        );
        bookCopy4.setDeleted(true);
        BookCopy bookCopy5 = new BookCopy(
                book2,
                new Location((short) 1, (short) 1, (short) 4)
        );
        BookCopy bookCopy6 = new BookCopy(
                book2,
                new Location((short) 1, (short) 3, (short) 2)
        );
        bookCopy6.setDeleted(true);
        BookCopy bookCopy7 = new BookCopy(
                book3,
                new Location((short) 2, (short) 4, (short) 1)
        );
        bookCopy7.setDeleted(true);
        BookCopy bookCopy8 = new BookCopy(
                book3,
                new Location((short) 2, (short) 4, (short) 1)
        );


        List<Book> books = Collections.unmodifiableList(Arrays.asList(book1, book2, book3));
        bookRepository.saveAll(books);
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2, bookCopy3, bookCopy4, bookCopy5, bookCopy6, bookCopy7, bookCopy8));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual)
                .hasSize(3)
                .containsOnly(bookCopy1, bookCopy3, bookCopy5);
    }

    @Test
    void listAvailablePageSizeSmallerThanTotalAmountOfBooks() {
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
        BookCopy bookCopy1 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        BookCopy bookCopy2 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 2)
        );
        BookCopy bookCopy3 = new BookCopy(
                book,
                new Location((short) 1, (short) 2, (short) 1)
        );
        BookCopy bookCopy4 = new BookCopy(
                book,
                new Location((short) 1, (short) 2, (short) 1)
        );

        bookRepository.save(book);
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2, bookCopy3, bookCopy4));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listAvailable(ofSize(3));

        // then
        // at least 3 book copies of the list "bookCopies" appear in actual
        assertThat(actual)
                .hasSize(3)
                .areAtLeast(3, new Condition<>(bookCopies::contains, "containsNBooksOf"));
    }

    @Test
    void listByBookNoBook() {
        // when
        List<BookCopy> actual = underTest.listByBook(1L, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByBookNoCopies() {
        // given
        Long bookId = 1L;
        Book book = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        book.setId(bookId);
        bookRepository.save(book);

        // when
        List<BookCopy> actual = underTest.listByBook(bookId, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByDeletedBook() {
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
        book.setDeleted(true);

        BookCopy bookCopy1 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        BookCopy bookCopy2 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 2)
        );

        bookRepository.save(book);
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listByBook(1L, ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listByBookMultipleAvailableCopies() {
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

        BookCopy bookCopy1 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        BookCopy bookCopy2 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 2)
        );


        Long bookId = bookRepository.save(book).getId();
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listByBook(bookId, ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(bookCopy1, bookCopy2);
    }

    @Test
    void listByBookMultipleAvailableAndDeletedCopies() {
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

        BookCopy bookCopy1 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy1.setDeleted(true);
        BookCopy bookCopy2 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 2)
        );
        BookCopy bookCopy3 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy3.setDeleted(true);
        BookCopy bookCopy4 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 2)
        );
        BookCopy bookCopy5 = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy5.setDeleted(true);

        Long bookId = bookRepository.save(book).getId();
        List<BookCopy> bookCopies = Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2, bookCopy3, bookCopy4, bookCopy5));
        underTest.saveAll(bookCopies);

        // when
        List<BookCopy> actual = underTest.listByBook(bookId, ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .containsOnly(bookCopy2, bookCopy4);
    }
}