package com.yer.library.repository;

import com.yer.library.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository underTest;

    @Test
    void findByIsbn() {
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
    void listAvailable() {
    }
}