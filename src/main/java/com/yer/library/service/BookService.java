package com.yer.library.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Book;
import com.yer.library.model.dtos.BookDTO;
import com.yer.library.model.dtos.jsonviews.View;
import com.yer.library.model.dtos.mappers.BookMapper;
import com.yer.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static org.springframework.data.domain.PageRequest.ofSize;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService implements CrudService<Book> {

    private final BookRepository bookRepository;
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Override
    public Book get(Long bookId) {
        log.info("Fetching book with ID: {}", bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with ID " + bookId + " does not exist"
                )
        );
        if (book.getDeleted()) {
            throw new IllegalStateException("book with ID " + bookId + " does not exist");
        }
        return book;
    }

    @Override
    public Collection<Book> list(int limit) {
        log.info("Listing all books (up to a limit of {})", limit);
        return bookRepository.listAvailable(ofSize(limit));
    }

    @Override
    public Book add(Book book) {
        log.info("Adding new book (ISBN = {})", book.getIsbn());
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(existingBook -> {
            if (existingBook.getDeleted()) {
                book.setId(existingBook.getId());
            } else {
                throw new IllegalStateException("ISBN " + book.getIsbn() + " already exists");
            }
        });
        return bookRepository.save(book);
    }


    public Book fullUpdate(Long bookId, Book updatedBook) {
        log.info("Updating book with ID: {}", bookId);
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with ID " + bookId + " does not exist"
                )
        );
        if (existingBook.getDeleted()) {
            throw new IllegalStateException(
                    "book with ID " + bookId + " has been deleted"
            );
        }

        Long updatedId = updatedBook.getId();
        if (updatedId != null && !updatedId.equals(bookId)) {
            log.warn("Cannot update internal book ID from {} to {}; saving under ID {}", bookId, updatedId, bookId);
        }

        if (!updatedBook.getIsbn().equals(existingBook.getIsbn())) {
            bookRepository.findByIsbn(updatedBook.getIsbn()).ifPresent(bookWithSameIsbn -> {
                if (!bookWithSameIsbn.getDeleted()) {
                    throw new IllegalStateException("ISBN " + bookWithSameIsbn.getIsbn() + " already exists");
                }
            });
        }

        updatedBook.setId(bookId);
        bookRepository.save(updatedBook);

        return updatedBook;
    }

    // TODO maybe make sure save() isn't called if the value isn't updated
    @Override
    public Book partialUpdate(Long bookId, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        log.info("Updating book with ID: {}", bookId);

        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with ID " + bookId + " does not exist"
                )
        );
        if (existingBook.getDeleted()) {
            throw new IllegalStateException(
                    "book with ID " + bookId + " has been deleted"
            );
        }

        BookDTO existingBookDTO = BookMapper.INSTANCE.toBookDTO(existingBook);

        // configure ObjectMapper instance to include JsonView in its deserializer config (View.PatchView.class in this case)
        mapper.setConfig(mapper.getDeserializationConfig()
                .withView(View.PatchView.class));

        JsonNode existingBookJson = mapper.convertValue(existingBookDTO, JsonNode.class);
        JsonNode patched = jsonPatch.apply(existingBookJson);

        BookDTO updatedBookDTO = mapper.treeToValue(patched, BookDTO.class);
        Book updatedBook = BookMapper.INSTANCE.toBook(updatedBookDTO);

        String updatedIsbn = updatedBook.getIsbn();
        if (updatedIsbn != null && !updatedIsbn.equals(existingBook.getIsbn())) {
            bookRepository.findByIsbn(updatedIsbn).ifPresent(bookWithSameIsbn -> {
                if (!bookWithSameIsbn.getDeleted()) {
                    throw new IllegalStateException("ISBN " + bookWithSameIsbn.getIsbn() + " already exists.");
                }
            });
        }

        updatedBook.setId(existingBook.getId());

        return bookRepository.save(updatedBook);
    }

    @Override
    public Boolean delete(Long bookId) {
        log.info("Deleting book with ID: {}", bookId);

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with ID " + bookId + " does not exist"
                )
        );

        if (book.getDeleted()) {
            throw new IllegalStateException(
                    "book with ID " + bookId + " has already been deleted"
            );
        }
        book.setDeleted(true);

        return Boolean.TRUE;
    }
}
