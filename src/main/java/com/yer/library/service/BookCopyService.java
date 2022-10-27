package com.yer.library.service;

import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.repository.BookCopyRepository;
import com.yer.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.ofSize;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookCopyService implements CrudService<BookCopy> {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopy get(Long bookCopyId) {
        log.info("Fetching book copy with ID: {}", bookCopyId);
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with ID " + bookCopyId + " does not exist"
                )
        );
        if (bookCopy.getBook().getDeleted()) {
            throw new IllegalStateException("book for book copy with ID " + bookCopyId + " has been deleted");
        }
        if (bookCopy.getDeleted()) {
            throw new IllegalStateException("book copy with ID " + bookCopyId + " does not exist");
        }
        return bookCopy;
    }

    @Override
    public Collection<BookCopy> list(int limit) {
        log.info("Listing all book copies (up to a limit of {})", limit);
        return bookCopyRepository.listAvailable(ofSize(limit));
    }

    public Collection<BookCopy> listByBook(Long bookId, int limit) {
        log.info("Listing all book copies for book with ID {} (up to a limit of {})", bookId, limit);
        return bookCopyRepository.listByBook(bookId, ofSize(limit));
    }

    @Override
    public BookCopy add(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    // TODO might want to change the signature to BookCopy add(BookCopy bookCopy, String bookIsbn)
    public BookCopy add(BookCopy bookCopy, Long bookId) {
        log.info("Adding new book copy");
        if (bookId == null) {
            throw new IllegalArgumentException("cannot add book copy without specifying a book ID");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("cannot add book copy for book: book with ID " + bookId + " does not exist")
        );
        if (book.getDeleted()) {
            throw new IllegalStateException("cannot add book copy for book: book with ID " + bookId + " has been deleted");
        }

        bookCopy.setBook(book);

        return add(bookCopy);
    }

    @Override
    public BookCopy fullUpdate(Long bookCopyId, BookCopy updateBookCopy) {
        if (!bookCopyRepository.existsById(bookCopyId)) {
            throw new IllegalStateException(
                    "book copy with ID " + bookCopyId + " does not exist"
            );
        }
        updateBookCopy.setId(bookCopyId);

        return bookCopyRepository.save(updateBookCopy);
    }

    public BookCopy fullUpdate(Long bookCopyId, BookCopy updateBookCopy, Long bookId) {
        log.info("Updating book copy with ID: {}", bookCopyId);

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("cannot update book copy for book: book with ID " + bookId + " does not exist")
        );
        if (book.getDeleted()) {
            throw new IllegalStateException("cannot update book copy for book: book with ID " + bookId + " has been deleted");
        }

        updateBookCopy.setBook(book);

        return fullUpdate(bookCopyId, updateBookCopy);
    }

    @Override
    public Boolean delete(Long bookCopyId) {
        log.info("Deleting book copy with ID: {}", bookCopyId);

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with ID " + bookCopyId + " does not exist"
                )
        );

        if (bookCopy.getDeleted()) {
            throw new IllegalStateException(
                    "book copy with ID " + bookCopyId + " has already been deleted"
            );
        }
        bookCopy.setDeleted(true);

        return TRUE;
    }

//    @Transactional
//    public BookCopy partialUpdate(Long bookCopyId, Short locFloor, Short locBookcase, Short locShelve) {
//        log.info("Updating book copy with id: {}", bookCopyId);
//        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
//                () -> new IllegalStateException(
//                        "book copy with id " + bookCopyId + " does not exist"
//                )
//        );
//
//        if (locFloor == null) locFloor = bookCopy.getLocation().getFloor();
//        if (locBookcase == null) locBookcase = bookCopy.getLocation().getBookcase();
//        if (locShelve == null) locShelve = bookCopy.getLocation().getShelve();
//
//        Location location = new Location(locFloor, locBookcase, locShelve);
//
//        if (!Objects.equals(bookCopy.getLocation(), location)) {
//            bookCopy.setLocation(location);
//        }
//
//        return bookCopy;
//    }
//
//    @Transactional
//    public BookCopy delete(Long bookCopyId) {
//        log.info("Deleting book copy with id: {}", bookCopyId);
//
//        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
//                () -> new IllegalStateException(
//                        "book copy with id " + bookCopyId + " does not exist"
//                )
//        );
//
//        if (bookCopy.getDeleted()) {
//            throw new IllegalStateException(
//                    "book copy with id " + bookCopyId + " has already been deleted"
//            );
//        }
//        bookCopy.setDeleted(true);
//
//        return bookCopy;
//    }

}
