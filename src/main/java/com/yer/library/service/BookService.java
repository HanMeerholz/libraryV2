package com.yer.library.service;


import com.yer.library.model.Book;
import com.yer.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Year;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.domain.PageRequest.of;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    public Book get(Long bookId) {
        log.info("Fetching book with id: {}", bookId);
        return bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookId + " does not exist"
                )
        );
    }

    public Collection<Book> list(int limit) {
        log.info("Listing all books");
        return bookRepository.findAll(of(0, limit)).toList();
    }

    public Collection<Book> listAvailable(int limit) {
        log.info("Listing all books");
        return bookRepository.listAvailableBooks(of(0, limit));
    }

    public Book add(Book book) {
        log.info("Adding new book (ISBN = {})", book.getIsbn());
        Optional<Book> bookByIsbn = bookRepository.findBookByIsbn(book.getIsbn());
        if (bookByIsbn.isPresent()) {
            throw new IllegalStateException("ISBN already exists.");
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long bookId, String isbn, String title, Year year, String author, Integer value) {
        log.info("Updating book with id: {}", bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookId + " does not exist"
                )
        );

        if (isbn != null &&
                isbn.length() > 0 &&
                !Objects.equals(book.getIsbn(), isbn)
        ) {
            if (bookRepository.findBookByIsbn(book.getIsbn()).isPresent()) {
                throw new IllegalStateException("ISBN already exists.");
            }
            book.setIsbn(isbn);
        }

        if (title != null &&
                title.length() > 0 &&
                !Objects.equals(book.getIsbn(), title)
        ) {
            book.setTitle(title);
        }

        if (year != null &&
                !Objects.equals(book.getYear(), year)
        ) {
            book.setYear(year);
        }

        if (author != null &&
                author.length() > 0 &&
                !Objects.equals(book.getAuthor(), author)
        ) {
            book.setAuthor(author);
        }

        if (value != null &&
                !Objects.equals(book.getValue(), value)
        ) {
            book.setValue(value);
        }

        return book;
    }

    @Transactional
    public Book delete(Long bookId) {
        log.info("Deleting book with id: {}", bookId);

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookId + " does not exist"
                )
        );

        if (book.getDeleted()) {
            throw new IllegalStateException(
                    "book with id " + bookId + " has already been deleted"
            );
        }
        book.setDeleted(true);

        return book;
    }
}
