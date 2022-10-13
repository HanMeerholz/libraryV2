package com.yer.library.resource;

import com.yer.library.model.Book;
import com.yer.library.model.Response;
import com.yer.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Year;

import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private static final int MAX_PAGE_SIZE = 50;
    private final BookService bookService;

    @GetMapping(path = "{bookId}")
    public ResponseEntity<Response> getBook(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookService.get(bookId)))
                        .message("Book " + bookId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Response> getBooks() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("books", bookService.list(MAX_PAGE_SIZE)))
                        .message("Books retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path="listAvailable")
    public ResponseEntity<Response> getAvailableBooks() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("books", bookService.listAvailable(MAX_PAGE_SIZE)))
                        .message("Books retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Response> addBook(@RequestBody @Valid Book book) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookService.add(book)))
                        .message("Book created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @PutMapping(path = "{bookId}")
    public ResponseEntity<Response> updateBook(
            @PathVariable("bookId") Long bookId,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Year year,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer value
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookService.update(bookId, isbn, title, year, author, value)))
                        .message("Book " + bookId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping(path = "{bookId}")
    public ResponseEntity<Response> deleteBook(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("delete", bookService.delete(bookId)))
                        .message("Book " + bookId + " deleted")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }
}
