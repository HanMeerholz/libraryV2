package com.yer.library.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.yer.library.model.BookCopy;
import com.yer.library.model.Response;
import com.yer.library.service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yer.library.resource.Constants.MAX_PAGE_SIZE;
import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "api/v1/book_copies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @GetMapping(path = "{bookCopyId}")
    public ResponseEntity<Response> getBookCopy(@PathVariable("bookCopyId") Long bookCopyId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copy", bookCopyService.get(bookCopyId)))
                        .message("Book copy " + bookCopyId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Response> getBookCopies() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copies", bookCopyService.list(MAX_PAGE_SIZE)))
                        .message("Book copies retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path = "/list_by_book/{bookId}")
    public ResponseEntity<Response> getBookCopies(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copy", bookCopyService.listByBook(bookId, MAX_PAGE_SIZE)))
                        .message("Book copies for book with ID " + bookId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Response> addBookCopy(@RequestParam Long bookId, @RequestBody @Valid BookCopy bookCopy) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copy", bookCopyService.add(bookCopy, bookId)))
                        .message("Book copy created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @PutMapping(path = "{bookCopyId}")
    public ResponseEntity<Response> updateBookCopy(
            @RequestParam Long bookId,
            @PathVariable("bookCopyId") Long bookCopyId,
            @RequestBody @Valid BookCopy bookCopy
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copy", bookCopyService.fullUpdate(bookCopyId, bookCopy, bookId)))
                        .message("Book copy " + bookCopyId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PatchMapping(path = "{bookCopyId}")
    public ResponseEntity<Response> partiallyUpdateBookCopy(
            @PathVariable("bookCopyId") Long bookCopyId,
            @RequestBody JsonPatch jsonPatch
    ) throws JsonPatchException, JsonProcessingException {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copy", bookCopyService.partialUpdate(bookCopyId, jsonPatch)))
                        .message("Book " + bookCopyId + " updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @DeleteMapping(path = "{bookCopyId}")
    public ResponseEntity<Response> deleteBookCopy(@PathVariable("bookCopyId") Long bookCopyId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("delete", bookCopyService.delete(bookCopyId)))
                        .message("Book copy " + bookCopyId + " deleted")
                        .status(NO_CONTENT)
                        .statusCode(NO_CONTENT.value())
                        .build()
        );
    }
}
