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
import static org.springframework.data.domain.PageRequest.of;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookCopyService implements CrudService<BookCopy> {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopy get(Long bookCopyId) {
        log.info("Fetching book copy with id: {}", bookCopyId);
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookCopyId + " does not exist"
                )
        );
        if (bookCopy.getDeleted()) {
            throw new IllegalStateException("book with id " + bookCopyId + " does not exist");
        }
        return bookCopy;
    }

    @Override
    public Collection<BookCopy> list(int limit) {
        log.info("Listing all book copies");
        return bookCopyRepository.listAvailable(of(0, limit));
    }

    public Collection<BookCopy> listByBook(Long bookId, int limit) {
        log.info("Listing all book copies for book with ID {}", bookId);
        return bookCopyRepository.listByBook(bookId, of(0, limit));
    }

    @Override
    public BookCopy add(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    // TODO might want to change the signature to BookCopy add(BookCopy bookCopy, String bookIsbn)
    public BookCopy add(BookCopy bookCopy, Long bookId) {
        log.info("Adding new book copy");
        if (bookId == null) {
            throw new IllegalArgumentException("Cannot add book copy without specifying a book ID");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("book copy with id " + bookId + " does not exist")
        );

        bookCopy.setBook(book);

        return add(bookCopy);
    }

    @Override
    public BookCopy fullUpdate(Long bookCopyId, BookCopy bookCopy) {
        log.info("Updating book copy with id: {}", bookCopyId);
        BookCopy updateBookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with id " + bookCopyId + " does not exist"
                )
        );

        bookCopy.setId(bookCopyId);
        bookCopy.setBook(updateBookCopy.getBook());
        bookCopyRepository.save(bookCopy);

        return bookCopy;
    }

    @Override
    public Boolean delete(Long bookCopyId) {
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

        return TRUE;
    }


//

//
//    public BookCopy add(Long bookId, BookCopy bookCopy) {
//        log.info("Adding new book copy");
//        if (bookId == null) {
//            throw new IllegalArgumentException("Cannot add book copy without specifying a book ID");
//        }
//
//        Book book = bookRepository.findById(bookId).orElseThrow(
//                () -> new IllegalStateException("book with id " + bookId + " does not exist")
//        );
//
//        bookCopy.setBook(book);
//        return bookCopyRepository.save(bookCopy);
//    }
//
//    @Transactional
//    public BookCopy update(Long bookCopyId, Short locFloor, Short locBookcase, Short locShelve) {
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
