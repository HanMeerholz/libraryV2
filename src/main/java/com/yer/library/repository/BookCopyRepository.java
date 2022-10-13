package com.yer.library.repository;

import com.yer.library.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    @Query("SELECT b FROM BookCopy b WHERE b.id = ?1")
    List<BookCopy> listByBook(Long bookId);
}
