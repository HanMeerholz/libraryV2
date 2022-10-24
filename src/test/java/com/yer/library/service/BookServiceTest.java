package com.yer.library.service;

import com.yer.library.model.Book;
import com.yer.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService underTest;

    @Test
    void getExistingNonDeletedBook() {
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
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        Book returnedBook = underTest.get(bookId);

        // then
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        assertThat(returnedBook).isEqualTo(book);
    }

    @Test
    void getExistingDeletedBook() {
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
        book.setDeleted(true);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        assertThatThrownBy(() -> underTest.get(bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with id " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getNonExistingBook() {
        // given
        Long bookId = 1L;

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.get(bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with id " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void list() {
        // given
        int limit = 100;

        // when
        underTest.list(limit);

        // then
        verify(bookRepository).listAvailable(
                argThat(pageable -> pageable.equals(Pageable.ofSize(limit)))
        );
    }

    @Test
    void addValidBook() {
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
        Book expectedReturnedBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        expectedReturnedBook.setId(bookId);
        given(bookRepository.save(book)).willReturn(expectedReturnedBook);

        // when
        Book returnedBook = underTest.add(book);

        // then
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook).isEqualTo(book);
        assertThat(returnedBook).isEqualTo(expectedReturnedBook);
    }

    @Test
    void addBookWithIsbnTakenByExistingBook() {
        // given
        String isbn = "978-2-3915-3957-4";
        Book book1 = new Book(
                isbn,
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        Book book2 = new Book(
                isbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(book1));

        // when
        // then
        assertThatThrownBy(() -> underTest.add(book2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ISBN " + book2.getIsbn() + " already exists");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBookWithIsbnTakenByDeletedBook() {
        // given
        Long deletedBookInDBId = 1L;
        String isbn = "978-2-3915-3957-4";
        Book deletedBookInDB = new Book(
                isbn,
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        deletedBookInDB.setId(deletedBookInDBId);
        deletedBookInDB.setDeleted(true);

        Book newBook = new Book(
                isbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );

        Book expectedReturnedBook = new Book(
                isbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );
        expectedReturnedBook.setId(deletedBookInDBId);

        given(bookRepository.save(newBook)).willReturn(expectedReturnedBook);
        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.of(deletedBookInDB));

        // when
        Book returnedBook = underTest.add(newBook);

        // then
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook).isEqualTo(expectedReturnedBook);
        assertThat(returnedBook).isEqualTo(expectedReturnedBook);
    }

    @Test
    void fullUpdateExistingBookSameIsbn() {
        // given
        Long bookId = 1L;
        String isbn = "978-2-3915-3957-4";
        Book initialBook = new Book(
                isbn,
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        initialBook.setId(bookId);

        Book updatedBook = new Book(
                isbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                "fiction",
                "horror",
                4200
        );

        Book expectedReturnedBook = new Book(
                isbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                "fiction",
                "horror",
                4200
        );
        expectedReturnedBook.setId(bookId);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(initialBook));
        given(bookRepository.save(updatedBook)).willReturn(expectedReturnedBook);

        // when
        Book returnedBook = underTest.fullUpdate(bookId, updatedBook);

        // then
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook).isEqualTo(updatedBook);
        assertThat(returnedBook).isEqualTo(expectedReturnedBook);
    }

    @Test
    void fullUpdateNonExistingBook() {
        // given
        Long bookId = 1L;
        String isbn = "978-2-3915-3957-4";
        Book updatedBook = new Book(
                isbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                "fiction",
                "horror",
                4200
        );
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookId, updatedBook))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with id " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void fullUpdateExistingBookNewIsbnExistsForNonDeletedBook() {
        // given
        Long book1Id = 1L;
        Book existingBook1 = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        existingBook1.setId(book1Id);

        String newIsbn = "978-2-3915-3957-5";
        Book existingBook2 = new Book(
                newIsbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );
        existingBook2.setId(1L);

        Book updatedBook = new Book(
                newIsbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                "fiction",
                "horror",
                4200
        );
        given(bookRepository.findById(book1Id)).willReturn(Optional.of(existingBook1));
        given(bookRepository.findByIsbn(newIsbn)).willReturn(Optional.of(existingBook2));

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(book1Id, updatedBook))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ISBN " + newIsbn + " already exists");
        verify(bookRepository).findById(
                argThat(id -> id.equals(book1Id))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteExistingNonDeletedBook() {
        // given
        Long bookId = 1L;
        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        existingBook.setId(bookId);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));

        // when
        Boolean result = underTest.delete(bookId);

        // then
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        assertThat(result).isTrue();
        assertThat(existingBook.getDeleted()).isTrue();
    }

    @Test
    void deleteExistingDeletedBook() {
        // given
        Long bookId = 1L;
        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        existingBook.setDeleted(true);
        existingBook.setId(bookId);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with id " + bookId + " has already been deleted");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
    }

    @Test
    void deleteNonExistingBook() {
        // given
        Long bookId = 1L;
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with id " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
    }
}