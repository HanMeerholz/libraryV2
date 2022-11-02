package com.yer.library.model.dtos.mappers;

import com.yer.library.model.Book;
import com.yer.library.model.dtos.BookDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDTO toBookDTO(Book book);

    Book toBook(BookDTO bookDTO);
}
