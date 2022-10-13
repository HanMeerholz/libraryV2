package com.yer.library.service;

import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.model.Location;
import com.yer.library.repository.BookCopyRepository;
import com.yer.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopy get(Long bookCopyId) {
        log.info("Fetching book copy with id: {}", bookCopyId);
        return bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookCopyId + " does not exist"
                )
        );
    }

    // TODO implement listByBook(Long bookId, int limit) method
    public Collection<BookCopy> listByBook(Long bookId) {
        log.info("Listing all book copies for book with ID {}", bookId);
        return bookCopyRepository.listByBook(bookId);
    }

    public BookCopy add(Long bookId, BookCopy bookCopy) {
        log.info("Adding new book copy");
        if (bookId == null) {
            throw new IllegalArgumentException("Cannot add book copy without specifying a book ID");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("book with id " + bookId + " does not exist")
        );

        bookCopy.setBook(book);
        return bookCopyRepository.save(bookCopy);
    }

    @Transactional
    public BookCopy update(Long bookCopyId, Short locFloor, Short locBookcase, Short locShelve) {
        log.info("Updating book copy with id: {}", bookCopyId);
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with id " + bookCopyId + " does not exist"
                )
        );

        if (locFloor == null) locFloor = bookCopy.getLocation().getFloor();
        if (locBookcase == null) locBookcase = bookCopy.getLocation().getBookcase();
        if (locShelve == null) locShelve = bookCopy.getLocation().getShelve();

        Location location = new Location(locFloor, locBookcase, locShelve);

        if (!Objects.equals(bookCopy.getLocation(), location)) {
            bookCopy.setLocation(location);
        }

        return bookCopy;
    }

    @Transactional
    public BookCopy delete(Long bookCopyId) {
        log.info("Deleting book copy with id: {}", bookCopyId);

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with id " + bookCopyId + " does not exist"
                )
        );

        if (bookCopy.getDeleted()) {
            throw new IllegalStateException(
                    "book copy with id " + bookCopyId + " has already been deleted"
            );
        }
        bookCopy.setDeleted(true);

        return bookCopy;
    }

}
