package com.yer.library.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.Book;
import com.yer.library.model.Response;
import com.yer.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yer.library.resource.Constants.MAX_PAGE_SIZE;
import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "api/v1/books")
@RequiredArgsConstructor
public class BookController {

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
    public ResponseEntity<Response> fullUpdateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody @Valid Book book
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookService.fullUpdate(bookId, book)))
                        .message("Book " + bookId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PatchMapping(path = "{bookId}", consumes = "application/json-patch+json")
    public ResponseEntity<Response> partiallyUpdateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody JsonPatch jsonPatch
    ) throws JsonPatchException, JsonProcessingException {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookService.partialUpdate(bookId, jsonPatch)))
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
                        .data(getDataMap("soft deleted", bookService.delete(bookId)))
                        .message("Book " + bookId + " deleted")
                        .status(NO_CONTENT)
                        .statusCode(NO_CONTENT.value())
                        .build()
        );
    }
}
