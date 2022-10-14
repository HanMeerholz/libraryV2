package com.yer.library.service;


import com.yer.library.model.Book;
import com.yer.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static org.springframework.data.domain.PageRequest.of;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService implements CrudService<Book> {

    private final BookRepository bookRepository;

    @Override
    public Book get(Long bookId) {
        log.info("Fetching book with id: {}", bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookId + " does not exist"
                )
        );
        if (book.getDeleted()) {
            throw new IllegalStateException("book with id " + bookId + " does not exist");
        }
        return book;
    }

    @Override
    public Collection<Book> list(int limit) {
        log.info("Listing all books");
        return bookRepository.listAvailable(of(0, limit));
    }

    @Override
    public Book add(Book book) {
        log.info("Adding new book (ISBN = {})", book.getIsbn());
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(existingBook -> {
            if (existingBook.getDeleted()) {
                book.setId(existingBook.getId());
            } else {
                throw new IllegalStateException("ISBN already exists.");
            }
        });
        return bookRepository.save(book);
    }


    //    @Transactional
//    public Book update(Long bookId, Book book) {
//        log.info("Updating book with id: {}", bookId);
//        Book updateBook = bookRepository.findById(bookId).orElseThrow(
//                () -> new IllegalStateException(
//                        "book with id " + bookId + " does not exist"
//                )
//        );
//
//        String isbn = book.getIsbn();
//        if (isbn != null &&
//                isbn.length() > 0 &&
//                !Objects.equals(book.getIsbn(), isbn)
//        ) {
//            if (bookRepository.findBookByIsbn(book.getIsbn()).isPresent()) {
//                throw new IllegalStateException("ISBN already exists.");
//            }
//            book.setIsbn(isbn);
//        }
//
//        String title = book.getTitle();
//        if (title != null &&
//                title.length() > 0 &&
//                !Objects.equals(book.getIsbn(), title)
//        ) {
//            book.setTitle(title);
//        }
//
//        Year year = book.getYear();
//        if (year != null &&
//                !Objects.equals(book.getYear(), year)
//        ) {
//            book.setYear(year);
//        }
//
//        String author = book.getAuthor();
//        if (author != null &&
//                author.length() > 0 &&
//                !Objects.equals(book.getAuthor(), author)
//        ) {
//            book.setAuthor(author);
//        }
//
//        Integer value = book.getValue();
//        if (value != null &&
//                !Objects.equals(book.getValue(), value)
//        ) {
//            book.setValue(value);
//        }
//
//        return book;
//    }
    public Book fullUpdate(Long bookId, Book book) {
        log.info("Updating book with id: {}", bookId);
        Book updateBook = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException(
                        "book with id " + bookId + " does not exist"
                )
        );

        if (!book.getIsbn().equals(updateBook.getIsbn()) &&
                !bookRepository.findByIsbn(updateBook.getIsbn()).orElseThrow(
                        () -> new IllegalStateException("ISBN already exists.")
                ).getDeleted()) {
            throw new IllegalStateException("ISBN already exists.");
        }

        book.setId(bookId);
        bookRepository.save(book);

        return book;
    }

    @Override
    public Boolean delete(Long bookId) {
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

        return Boolean.TRUE;
    }
}
