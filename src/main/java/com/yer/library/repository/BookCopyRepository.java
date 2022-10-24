package com.yer.library.repository;

import com.yer.library.model.BookCopy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    @Query("SELECT b FROM BookCopy b WHERE b.deleted = false AND b.book.deleted = false")
    List<BookCopy> listAvailable(Pageable pageable);

    @Query("SELECT b FROM BookCopy b WHERE b.book.id = ?1 AND b.book.deleted = false AND b.deleted = false")
    List<BookCopy> listByBook(Long bookId, Pageable pageable);
}
