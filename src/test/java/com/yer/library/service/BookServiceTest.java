package com.yer.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Book;
import com.yer.library.model.enums.BookGenre;
import com.yer.library.model.enums.BookType;
import com.yer.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Logger logger;

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
                BookType.FICTION,
                BookGenre.HORROR,
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        book.setId(bookId);
        book.setDeleted(true);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        assertThatThrownBy(() -> underTest.get(bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + bookId + " does not exist");
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
                .hasMessageContaining("book with ID " + bookId + " does not exist");
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Book expectedReturnedBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        Book book2 = new Book(
                isbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                BookType.FICTION,
                BookGenre.ROMANCE,
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        deletedBookInDB.setId(deletedBookInDBId);
        deletedBookInDB.setDeleted(true);

        Book newBook = new Book(
                isbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                BookType.FICTION,
                BookGenre.ROMANCE,
                4200
        );

        Book expectedReturnedBook = new Book(
                isbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                BookType.FICTION,
                BookGenre.ROMANCE,
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        initialBook.setId(bookId);

        Book updatedBook = new Book(
                isbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );

        Book expectedReturnedBook = new Book(
                isbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                BookType.FICTION,
                BookGenre.HORROR,
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
    void fullUpdateExistingBookNewId() {
        // given
        Long bookId = 1L;
        Book initialBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        initialBook.setId(bookId);

        Long newBookId = 2L;
        Book updatedBook = new Book(
                "978-2-3915-3957-4",
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        updatedBook.setId(newBookId);

        Book expectedReturnedBook = new Book(
                "978-2-3915-3957-4",
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                BookType.FICTION,
                BookGenre.HORROR,
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
        verify(logger).warn("Cannot update internal book ID from {} to {}; saving under ID {}", bookId, newBookId, bookId);

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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookId, updatedBook))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void fullUpdateExistingDeletedBook() {
        // given
        Long bookId = 1L;

        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook.setId(bookId);
        existingBook.setDeleted(true);

        Book updatedBook = new Book(
                "978-2-3915-3957-4",
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(bookId, updatedBook))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + bookId + " has been deleted");
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook1.setId(book1Id);

        Long book2Id = 2L;
        String newIsbn = "978-2-3915-3957-5";
        Book existingBook2 = new Book(
                newIsbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                BookType.FICTION,
                BookGenre.ROMANCE,
                4200
        );
        existingBook2.setId(book2Id);

        Book updatedBook = new Book(
                newIsbn,
                "The Girls in the Veils",
                Year.of(1947),
                "Cole Lyon",
                BookType.FICTION,
                BookGenre.HORROR,
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
    void partialUpdateExistingBookSameIsbn() throws IOException, JsonPatchException {
        // given
        Long bookId = 1L;
        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook.setId(bookId);

        @SuppressWarnings("JsonStandardCompliance") String updatedTitle = "The Boy in the Veil";

        Book updatedBook = new Book(
                "978-2-3915-3957-4",
                updatedTitle,
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        updatedBook.setId(bookId);

        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/title\"," +
                "\"value\": \"" + updatedTitle + "\"" +
                "}]";
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        JsonNode bookJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookJson);

        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));
        given(bookRepository.save(updatedBook)).willReturn(updatedBook);

        // when
        Book returnedBook = underTest.partialUpdate(bookId, jsonPatch);

        // then
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook).isEqualTo(updatedBook);
        assertThat(returnedBook).isEqualTo(updatedBook);
        assertThat(returnedBook.getTitle()).isEqualTo(updatedTitle);
    }

    @Test
    void partialUpdateNonExistingBook() throws IOException {
        // given
        Long bookId = 1L;

        @SuppressWarnings("JsonStandardCompliance") String updatedTitle = "The Boy in the Veil";

        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/title\"," +
                "\"value\": \"" + updatedTitle + "\"" +
                "}]";
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        JsonNode bookJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookJson);
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(bookId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void partialUpdateExistingDeletedBook() throws IOException {
        // given
        Long bookId = 1L;

        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook.setId(bookId);
        existingBook.setDeleted(true);

        @SuppressWarnings("JsonStandardCompliance") String updatedTitle = "The Boy in the Veil";

        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/title\"," +
                "\"value\": \"" + updatedTitle + "\"" +
                "}]";
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        JsonNode bookJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookJson);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(bookId, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + bookId + " has been deleted");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
        verify(bookRepository, never()).save(any());
    }

    @Test
    void partialUpdateExistingBookNewIsbnExistsForNonDeletedBook() throws IOException {
        // given
        Long book1Id = 1L;
        Book existingBook1 = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook1.setId(book1Id);

        Long book2Id = 2L;
        @SuppressWarnings("JsonStandardCompliance") String newIsbn = "978-2-3915-3957-5";
        Book existingBook2 = new Book(
                newIsbn,
                "Legacy Circling",
                Year.of(2001),
                "Arla Salgado",
                BookType.FICTION,
                BookGenre.ROMANCE,
                4200
        );
        existingBook2.setId(book2Id);

        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/isbn\"," +
                "\"value\": \"" + newIsbn + "\"" +
                "}]";
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        JsonNode bookJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookJson);

        given(bookRepository.findById(book1Id)).willReturn(Optional.of(existingBook1));
        given(bookRepository.findByIsbn(newIsbn)).willReturn(Optional.of(existingBook2));

        // when
        // then
        assertThatThrownBy(() -> underTest.partialUpdate(book1Id, jsonPatch))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ISBN " + newIsbn + " already exists");
        verify(bookRepository).findById(
                argThat(id -> id.equals(book1Id))
        );
        verify(bookRepository, never()).save(any());
    }

    // TODO make sure updating ID throws an exception, rather than has no effect
    @Test
    void partialUpdateBookId() throws IOException, JsonPatchException {
        // given
        Long bookId = 1L;

        Book existingBook = new Book(
                "978-2-3915-3957-4",
                "The Girl in the Veil",
                Year.of(1948),
                "Cole Lyons",
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook.setId(bookId);

        long updatedId = 2L;

        String jsonString = "[{" +
                "\"op\": \"replace\"," +
                "\"path\": \"/id\"," +
                "\"value\": \"" + updatedId + "\"" +
                "}]";
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        JsonNode bookJson = mapper.readTree(jsonString);
        JsonPatch jsonPatch = JsonPatch.fromJson(bookJson);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));
        given(bookRepository.save(existingBook)).willReturn(existingBook);

        // when
        Book returnedBook = underTest.partialUpdate(bookId, jsonPatch);

        // then
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );

        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook).isEqualTo(existingBook);
        assertThat(returnedBook).isEqualTo(existingBook);
        assertThat(returnedBook.getId()).isEqualTo(bookId);
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
                BookType.FICTION,
                BookGenre.HORROR,
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
                BookType.FICTION,
                BookGenre.HORROR,
                4200
        );
        existingBook.setDeleted(true);
        existingBook.setId(bookId);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(existingBook));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(bookId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("book with ID " + bookId + " has already been deleted");
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
                .hasMessageContaining("book with ID " + bookId + " does not exist");
        verify(bookRepository).findById(
                argThat(id -> id.equals(bookId))
        );
    }
}