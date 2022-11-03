package com.yer.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.model.dtos.BookCopyDTO;
import com.yer.library.model.dtos.jsonviews.View;
import com.yer.library.model.dtos.mappers.BookCopyMapper;
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
    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

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
    public BookCopy fullUpdate(Long bookCopyId, BookCopy updatedBookCopy) {
        BookCopy existingBookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with ID " + bookCopyId + " does not exist"
                )
        );
        if (existingBookCopy.getDeleted()) {
            throw new IllegalStateException(
                    "book copy with ID " + bookCopyId + " has been deleted"
            );
        }
        updatedBookCopy.setId(bookCopyId);

        return bookCopyRepository.save(updatedBookCopy);
    }

    public BookCopy fullUpdate(Long bookCopyId, BookCopy updatedBookCopy, Long bookId) {
        log.info("Updating book copy with ID: {}", bookCopyId);

        Long updatedId = updatedBookCopy.getId();
        if (updatedId != null && !updatedId.equals(bookId)) {
            log.warn("Cannot update internal book copy ID from {} to {}; saving under ID {}", bookId, updatedId, bookId);
        }

        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("cannot update book copy for book: book with ID " + bookId + " does not exist")
        );
        if (book.getDeleted()) {
            throw new IllegalStateException("cannot update book copy for book: book with ID " + bookId + " has been deleted");
        }

        updatedBookCopy.setBook(book);

        return fullUpdate(bookCopyId, updatedBookCopy);
    }

    @Override
    public BookCopy partialUpdate(Long bookCopyId, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        log.info("Updating book copy with ID: {}", bookCopyId);

        BookCopy existingBookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(
                () -> new IllegalStateException(
                        "book copy with ID " + bookCopyId + " does not exist"
                )
        );
        if (existingBookCopy.getDeleted()) {
            throw new IllegalStateException(
                    "book copy with ID " + bookCopyId + " has been deleted"
            );
        }

        BookCopyDTO existingBookCopyDTO = BookCopyMapper.INSTANCE.toBookCopyDTO(existingBookCopy);

        // configure ObjectMapper instance to include JsonView in its deserializer config (View.PatchView.class in this case)
        mapper.setConfig(mapper.getDeserializationConfig()
                .withView(View.PatchView.class));

        JsonNode existingBookCopyJson = mapper.convertValue(existingBookCopyDTO, JsonNode.class);
        JsonNode patched = jsonPatch.apply(existingBookCopyJson);

        BookCopyDTO updatedBookCopyDTO = mapper.treeToValue(patched, BookCopyDTO.class);
        BookCopy updatedBookCopy = BookCopyMapper.INSTANCE.toBookCopy(updatedBookCopyDTO, bookRepository);

        updatedBookCopy.setId(existingBookCopy.getId());

        return bookCopyRepository.save(updatedBookCopy);
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
}
