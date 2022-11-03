package com.yer.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.model.Location;
import com.yer.library.repository.BookCopyRepository;
import com.yer.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    @Spy
    private BookCopyService underTest;

    @Test
    void getExistingNonDeletedBookCopy() {
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

        Long bookCopyId = 1L;
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy.setId(bookCopyId);

        given(bookCopyRepository.findById(bookId)).willReturn(Optional.of(bookCopy));

        // when
        BookCopy returnedBookCopy = underTest.get(bookCopyId);

        // then
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        assertThat(returnedBookCopy).isEqualTo(bookCopy);
    }

    @Test
    void getExistingDeletedBookCopy() {
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

        Long bookCopyId = 1L;
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy.setId(bookCopyId);
        bookCopy.setDeleted(true);

        given(bookCopyRepository.findById(bookId)).willReturn(Optional.of(bookCopy));

        // when
        assertThatThrownBy(() -> underTest.get(bookCopyId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " does not exist");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void getExistingBookCopyForDeletedBook() {
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

        Long bookCopyId = 1L;
        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        bookCopy.setId(bookCopyId);

        given(bookCopyRepository.findById(bookId)).willReturn(Optional.of(bookCopy));

        // when
        // then
        assertThatThrownBy(() -> underTest.get(bookCopyId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book for book copy with ID " + bookCopyId + " has been deleted");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void getNonExistingBookCopy() {
        // given
        Long bookCopyId = 1L;

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.get(bookCopyId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " does not exist");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void list() {
        // given
        int limit = 100;

        // when
        underTest.list(limit);

        // then
        verify(bookCopyRepository).listAvailable(
                argThat(pageable -> pageable.equals(Pageable.ofSize(limit)))
        );
    }

    @Test
    void listByBook() {
        // given
        Long bookId = 1L;
        int limit = 100;

        // when
        underTest.listByBook(bookId, limit);

        // then
        verify(bookCopyRepository).listByBook(eq(bookId), argThat(
                pageable -> pageable.equals(Pageable.ofSize(limit))
        ));
    }

    @Test
    void addValidBookCopyWithBook() {
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

        BookCopy bookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        Long bookCopyId = 1L;
        BookCopy expectedReturnedCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        expectedReturnedCopy.setId(bookCopyId);

        given(bookCopyRepository.save(bookCopy)).willReturn(expectedReturnedCopy);

        // when
        BookCopy returnedCopy = underTest.add(bookCopy);

        // then
        ArgumentCaptor<BookCopy> bookCopyArgumentCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(bookCopyRepository).save(bookCopyArgumentCaptor.capture());
        BookCopy capturedBookCopy = bookCopyArgumentCaptor.getValue();

        assertThat(capturedBookCopy).isEqualTo(bookCopy);
        assertThat(returnedCopy).isEqualTo(expectedReturnedCopy);
    }

    @Test
    void addBookCopyWithoutBookId() {
        // given
        BookCopy bookCopy = new BookCopy(
                null,
                new Location((short) 1, (short) 1, (short) 1)
        );

        // when
        // then
        assertThatThrownBy(() -> underTest.add(bookCopy, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot add book copy without specifying a book ID");
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void addBookCopyForNonExistingBook() {
        // given
        Long bookId = 1L;
        BookCopy bookCopy = new BookCopy(
                null,
                new Location((short) 1, (short) 1, (short) 1)
        );
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.add(bookCopy, bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot add book copy for book: book with ID " + bookId + " does not exist");
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void addBookCopyForDeletedBook() {
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

        BookCopy bookCopy = new BookCopy(
                null,
                new Location((short) 1, (short) 1, (short) 1)
        );
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        // then
        assertThatThrownBy(() -> underTest.add(bookCopy, bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot add book copy for book: book with ID " + bookId + " has been deleted");
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void addValidBookCopy() {
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

        BookCopy bookCopy = new BookCopy(
                null,
                new Location((short) 1, (short) 1, (short) 1)
        );
        BookCopy bookCopyWithBook = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        Long bookCopyId = 1L;
        BookCopy expectedReturnedCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        expectedReturnedCopy.setId(bookCopyId);

        willReturn(Optional.of(book)).given(bookRepository).findById(bookId);
        // We pass in an "any" argument since the specific bookCopy has a null id and
        // will return false if .equals() is called on it
        willReturn(expectedReturnedCopy).given(underTest).add(any(BookCopy.class));

        // when
        BookCopy returnedCopy = underTest.add(bookCopy, bookId);

        // then
        ArgumentCaptor<BookCopy> bookCopyArgumentCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(underTest).add(bookCopyArgumentCaptor.capture());
        BookCopy capturedBookCopy = bookCopyArgumentCaptor.getValue();

        // Since bookCopyId is null, equals() won't work, so we compare fields instead
        assertThat(capturedBookCopy.getId()).isEqualTo(bookCopyWithBook.getId());
        assertThat(capturedBookCopy.getBook()).isEqualTo(bookCopyWithBook.getBook());
        assertThat(capturedBookCopy.getLocation()).isEqualTo(bookCopyWithBook.getLocation());
        assertThat(capturedBookCopy.getDeleted()).isEqualTo(bookCopyWithBook.getDeleted());

        assertThat(returnedCopy).isEqualTo(expectedReturnedCopy);
    }

    @Test
    void fullUpdateBookCopyWithBook() {
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

        Long bookCopyId = 1L;
        BookCopy initialBookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        initialBookCopy.setId(bookCopyId);
        BookCopy updatedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        BookCopy expectedReturnedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        expectedReturnedBookCopy.setId(bookCopyId);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(initialBookCopy));
        given(bookCopyRepository.save(updatedBookCopy)).willReturn(expectedReturnedBookCopy);

        // when
        BookCopy returnedCopy = underTest.fullUpdate(bookCopyId, updatedBookCopy);

        // then
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        ArgumentCaptor<BookCopy> bookCopyArgumentCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(bookCopyRepository).save(bookCopyArgumentCaptor.capture());
        BookCopy capturedBookCopy = bookCopyArgumentCaptor.getValue();

        assertThat(capturedBookCopy).isEqualTo(updatedBookCopy);
        assertThat(returnedCopy).isEqualTo(expectedReturnedBookCopy);
    }

    @Test
    void fullUpdateNonExistingBookCopyWithBook() {
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

        Long bookCopyId = 1L;
        BookCopy updatedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        BookCopy expectedReturnedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        expectedReturnedBookCopy.setId(bookCopyId);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookCopyId, updatedBookCopy))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " does not exist");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void fullUpdateExistingDeletedBookCopyWithBook() {
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

        Long bookCopyId = 1L;

        BookCopy existingBookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        existingBookCopy.setId(bookCopyId);
        existingBookCopy.setDeleted(true);

        BookCopy updatedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        BookCopy expectedReturnedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        expectedReturnedBookCopy.setId(bookCopyId);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookCopyId, updatedBookCopy))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " has been deleted");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void fullUpdateBookCopyForNonExistingBook() {
        // given
        BookCopy updatedBookCopy = new BookCopy(
                null,
                new Location((short) 2, (short) 2, (short) 2)
        );
        Long bookCopyId = 1L;

        Long bookId = 1L;

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookCopyId, updatedBookCopy, bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot update book copy for book: book with ID " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(underTest, never()).fullUpdate(any(), any());
    }

    @Test
    void fullUpdateBookCopyForDeletedBook() {
        // given
        BookCopy updatedBookCopy = new BookCopy(
                null,
                new Location((short) 2, (short) 2, (short) 2)
        );
        Long bookCopyId = 1L;

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
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookCopyId, updatedBookCopy, bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot update book copy for book: book with ID " + bookId + " has been deleted");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(underTest, never()).fullUpdate(any(), any());
    }

    @Test
    void fullUpdateValidBookCopy() {
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

        BookCopy updatedBookCopy = new BookCopy(
                null,
                new Location((short) 2, (short) 2, (short) 2)
        );
        Long bookCopyId = 1L;

        BookCopy updatedBookCopyWithBook = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );

        BookCopy expectedReturnedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        expectedReturnedBookCopy.setId(bookCopyId);

        willReturn(Optional.of(book)).given(bookRepository).findById(bookId);
        // once again expected with any() argument because of BookCopy#equals() returning false if id is null
        willReturn(expectedReturnedBookCopy).given(underTest).fullUpdate(eq(bookCopyId), any(BookCopy.class));

        // when
        BookCopy returnedBookCopy = underTest.fullUpdate(bookCopyId, updatedBookCopy, bookId);

        // then
        ArgumentCaptor<BookCopy> bookCopyArgumentCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(bookCopyId)),
                bookCopyArgumentCaptor.capture()
        );
        BookCopy capturedBookCopy = bookCopyArgumentCaptor.getValue();
        // compare fields instead of object directly, because BookCopy#equals() returns "false"
        // if id is null
        assertThat(capturedBookCopy.getId()).isEqualTo(updatedBookCopyWithBook.getId());
        assertThat(capturedBookCopy.getBook()).isEqualTo(updatedBookCopyWithBook.getBook());
        assertThat(capturedBookCopy.getLocation()).isEqualTo(updatedBookCopyWithBook.getLocation());
        assertThat(capturedBookCopy.getDeleted()).isEqualTo(updatedBookCopyWithBook.getDeleted());

        assertThat(returnedBookCopy).isEqualTo(expectedReturnedBookCopy);
    }

    @Test
    void fullUpdateValidBookCopyNewId() {
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

        Long newBookCopyId = 2L;
        BookCopy updatedBookCopy = new BookCopy(
                null,
                new Location((short) 2, (short) 2, (short) 2)
        );
        updatedBookCopy.setId(newBookCopyId);
        Long bookCopyId = 1L;

        BookCopy updatedBookCopyWithBook = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        updatedBookCopyWithBook.setId(newBookCopyId);

        BookCopy expectedReturnedBookCopy = new BookCopy(
                book,
                new Location((short) 2, (short) 2, (short) 2)
        );
        expectedReturnedBookCopy.setId(bookCopyId);

        willReturn(Optional.of(book)).given(bookRepository).findById(bookId);
        // once again expected with any() argument because of BookCopy#equals() returning false if id is null
        willReturn(expectedReturnedBookCopy).given(underTest).fullUpdate(eq(bookCopyId), any(BookCopy.class));

        // when
        BookCopy returnedBookCopy = underTest.fullUpdate(bookCopyId, updatedBookCopy, bookId);

        // then
        ArgumentCaptor<BookCopy> bookCopyArgumentCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(underTest).fullUpdate(
                argThat(id -> id.equals(bookCopyId)),
                bookCopyArgumentCaptor.capture()
        );
        verify(logger).warn("Cannot update internal book copy ID from {} to {}; saving under ID {}", bookCopyId, newBookCopyId, bookCopyId);

        BookCopy capturedBookCopy = bookCopyArgumentCaptor.getValue();
        // compare fields instead of object directly, because BookCopy#equals() returns "false"
        // if id is null
        assertThat(capturedBookCopy.getId()).isEqualTo(updatedBookCopyWithBook.getId());
        assertThat(capturedBookCopy.getBook()).isEqualTo(updatedBookCopyWithBook.getBook());
        assertThat(capturedBookCopy.getLocation()).isEqualTo(updatedBookCopyWithBook.getLocation());
        assertThat(capturedBookCopy.getDeleted()).isEqualTo(updatedBookCopyWithBook.getDeleted());

        assertThat(returnedBookCopy).isEqualTo(expectedReturnedBookCopy);
    }

    @Test
    void partialUpdateValidBookCopy() throws IOException, JsonPatchException {
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

        Long bookCopyId = 1L;
        BookCopy existingBookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        existingBookCopy.setId(bookCopyId);

        Location updatedLocation = new Location((short) 2, (short) 2, (short) 2);

        BookCopy updatedBookCopy = new BookCopy(
                book,
                updatedLocation
        );
        updatedBookCopy.setId(bookCopyId);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/location\"," +
                "\"value\":" + mapper.convertValue(updatedLocation, JsonNode.class) +
                "}]";
        JsonNode bookCopyJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookCopyJson);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookCopyRepository.save(updatedBookCopy)).willReturn(updatedBookCopy);

        // when
        BookCopy returnedBookCopy = underTest.partialUpdate(bookCopyId, jsonPatch);

        // then
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );

        ArgumentCaptor<BookCopy> bookCopyArgumentCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(bookCopyRepository).save(bookCopyArgumentCaptor.capture());
        BookCopy capturedBookCopy = bookCopyArgumentCaptor.getValue();

        assertThat(capturedBookCopy).isEqualTo(updatedBookCopy);
        assertThat(returnedBookCopy).isEqualTo(updatedBookCopy);
        assertThat(returnedBookCopy.getLocation()).isEqualTo(updatedLocation);
    }


    @Test
    void partialUpdateNonExistingBookCopy() throws IOException {
        // given
        Long bookCopyId = 1L;

        Location updatedLocation = new Location((short) 2, (short) 2, (short) 2);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/location\"," +
                "\"value\":" + mapper.convertValue(updatedLocation, JsonNode.class) +
                "}]";
        JsonNode bookCopyJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookCopyJson);
        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(bookCopyId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " does not exist");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void partialUpdateExistingDeletedBookCopy() throws IOException {
        // given
        Long existingBookId = 1L;
        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        existingBook.setId(existingBookId);

        Long bookCopyId = 1L;
        BookCopy existingBookCopy = new BookCopy(
                existingBook,
                new Location((short) 1, (short) 1, (short) 1)
        );
        existingBookCopy.setId(bookCopyId);
        existingBookCopy.setDeleted(true);

        Location updatedLocation = new Location((short) 2, (short) 2, (short) 2);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/location\"," +
                "\"value\":" + mapper.convertValue(updatedLocation, JsonNode.class) +
                "}]";
        JsonNode bookCopyJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookCopyJson);
        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(bookCopyId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " has been deleted");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void partialUpdateBookCopyBookForNonExistingBook() throws IOException {
        // given
        Long existingBookId = 1L;
        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        existingBook.setId(existingBookId);

        Long bookCopyId = 1L;
        BookCopy existingBookCopy = new BookCopy(
                existingBook,
                new Location((short) 1, (short) 1, (short) 1)
        );
        existingBookCopy.setId(bookCopyId);

        Long updatedBookId = 2L;

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/bookId\"," +
                "\"value\":" + updatedBookId +
                "}]";
        JsonNode bookCopyJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookCopyJson);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));
        given(bookRepository.findById(updatedBookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(bookCopyId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + updatedBookId + " does not exist");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }

    @Test
    void partialUpdateBookCopyBookForExistingDeletedBook() throws IOException {
        // given
        Long existingBookId = 1L;
        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                "fiction",
                "horror",
                4200
        );
        existingBook.setId(existingBookId);

        Long existingBook2Id = 2L;
        Book existingBook2 = new Book(
                "978-2-3915-3957-5",
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                "fiction",
                "romantic drama",
                4200
        );
        existingBook2.setId(existingBook2Id);
        existingBook2.setDeleted(true);

        Long bookCopyId = 1L;
        BookCopy existingBookCopy = new BookCopy(
                existingBook,
                new Location((short) 1, (short) 1, (short) 1)
        );
        existingBookCopy.setId(bookCopyId);

        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/bookId\"," +
                "\"value\":" + existingBook2Id +
                "}]";
        JsonNode bookCopyJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookCopyJson);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));
        given(bookRepository.findById(existingBook2Id)).willReturn(Optional.of(existingBook2));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(bookCopyId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + existingBook2Id + " has been deleted");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        verify(bookCopyRepository, never()).save(any());
    }


    @Test
    void deleteExistingNonDeletedBookCopy() {
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

        Long bookCopyId = 1L;
        BookCopy existingBookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));

        // when
        Boolean result = underTest.delete(bookCopyId);

        // then
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
        assertThat(result).isTrue();
        assertThat(existingBookCopy.getDeleted()).isTrue();
    }

    @Test
    void deleteExistingDeletedBookCopy() {
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

        Long bookCopyId = 1L;
        BookCopy existingBookCopy = new BookCopy(
                book,
                new Location((short) 1, (short) 1, (short) 1)
        );
        existingBookCopy.setDeleted(true);

        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.of(existingBookCopy));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(bookCopyId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookId + " has already been deleted");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookId))
        );
    }

    @Test
    void deleteNonExistingBookCopy() {
        // given
        Long bookCopyId = 1L;
        given(bookCopyRepository.findById(bookCopyId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(bookCopyId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book copy with ID " + bookCopyId + " does not exist");
        verify(bookCopyRepository).findById(
                argThat(id -> id.equals(bookCopyId))
        );
    }
}