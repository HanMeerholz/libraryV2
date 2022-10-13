package com.yer.library.resource;

import com.yer.library.model.BookCopy;
import com.yer.library.model.Response;
import com.yer.library.service.BookCopyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yer.library.resource.ControllerUtil.getDataMap;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "api/v1/bookcopies")
@RequiredArgsConstructor
public class BookCopyController {
    private final BookCopyService bookCopyService;

    @GetMapping(path = "{bookCopyId}")
    public ResponseEntity<Response> getBookCopy(@PathVariable("bookCopyId") Long bookCopyId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookCopyService.get(bookCopyId)))
                        .message("Book copy " + bookCopyId + " retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping(path = "/listByBook/{bookId}")
    public ResponseEntity<Response> getBookCopies(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book", bookCopyService.listByBook(bookId)))
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
                        .data(getDataMap("book_copy", bookCopyService.add(bookId, bookCopy)))
                        .message("Book copy created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @PutMapping(path = "{bookCopyId}")
    public ResponseEntity<Response> updateBookCopy(
            @PathVariable("bookCopyId") Long bookCopyId,
            @RequestParam(required = false) Short locFloor,
            @RequestParam(required = false) Short locBookcase,
            @RequestParam(required = false) Short locShelve
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .data(getDataMap("book_copy", bookCopyService.update(bookCopyId, locFloor, locBookcase, locShelve)))
                        .message("Book copy " + bookCopyId + " updated")
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
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }


}
