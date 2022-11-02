package com.yer.library.model.dtos.mappers;

import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.model.dtos.BookCopyDTO;
import com.yer.library.repository.BookRepository;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    BookCopyMapper INSTANCE = Mappers.getMapper(BookCopyMapper.class);

    @Mapping(target = "bookId", expression = "java(bookCopy.getId())")
    BookCopyDTO toBookCopyDTO(BookCopy bookCopy);

    @Mapping(target = "book", ignore = true)
    BookCopy toBookCopy(BookCopyDTO bookCopyDTO, @Context BookRepository bookRepository);

    @AfterMapping
    default void toBookCopy(@MappingTarget BookCopy bookCopy, BookCopyDTO bookCopyDTO, @Context BookRepository bookRepository) {
        Long bookId = bookCopyDTO.getBookId();
        Book book = bookRepository.findById(bookId).orElseThrow(() ->
                new IllegalStateException("book with ID " + bookId + " does not exist")
        );
        if (book.getDeleted()) {
            throw new IllegalStateException("book with ID " + bookId + " has been deleted");
        }
        bookCopy.setBook(book);
    }
}
