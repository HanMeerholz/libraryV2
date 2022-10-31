package com.yer.library.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Book;
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


    @Transactional
    public Book partialUpdate(Long bookId, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        log.info("Updating book with ID: {}", bookId);

        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with ID " + bookId + " does not exist"
                )
        );

        JsonNode existingBookJson = mapper.convertValue(existingBook, JsonNode.class);
        JsonNode patched = jsonPatch.apply(existingBookJson);

        Book updatedBook = mapper.treeToValue(patched, Book.class);

        if (!updatedBook.getIsbn().equals(existingBook.getIsbn())) {
            bookRepository.findByIsbn(updatedBook.getIsbn()).ifPresent(bookWithSameIsbn -> {
                if (!bookWithSameIsbn.getDeleted()) {
                    throw new IllegalStateException("ISBN " + bookWithSameIsbn.getIsbn() + " already exists.");
                }
            });
        }

        return bookRepository.save(updatedBook);
    }

    public Book fullUpdate(Long bookId, Book updatedBook) {
        log.info("Updating book with ID: {}", bookId);
        Book existingBook = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with ID " + bookId + " does not exist"
                )
        );

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
